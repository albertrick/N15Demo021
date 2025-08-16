package com.example.n15demo021;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private ListView lV;
    public IntentFilter downloadFilter;

    public ArrayList<String> fileData;
    public ArrayAdapter<String> adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");
        lV = findViewById(R.id.lV);

        fileData = new ArrayList<>();
        adp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, fileData);
        lV.setAdapter(adp);

        downloadFilter = new IntentFilter(DownloadService.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, downloadFilter);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }

    public void getData(View view) {
        Log.i(TAG, "getData");
        Intent serviceIntent = new Intent(this, DownloadService.class);
        startService(serviceIntent);
    }

    public ArrayList<String> parseJsonData(String jsonData) {
        Log.i(TAG, "parseJsonData");
        String all = "";
        ArrayList<String> dataList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray exchangeRates = jsonObject.getJSONArray("exchangeRates");
            for (int i = 0; i < exchangeRates.length(); i++) {
                JSONObject currencyData = exchangeRates.getJSONObject(i);
                String key = currencyData.getString("key");
                double currentExchangeRate = currencyData.getDouble("currentExchangeRate");
                double currentChange = currencyData.getDouble("currentChange");
                int unit = currencyData.getInt("unit");
                String lastUpdate = currencyData.getString("lastUpdate");
                all += key + ", ";
                all += currentExchangeRate + ", ";
                all += unit;
                dataList.add(all);
                all = "";
            }
            Log.d(TAG, "parseJsonData dataList: " + dataList.toString());
        } catch (JSONException e) {
            Log.e("MainActivity","Error parsing JSON: " + e.getMessage());
        }
        return dataList;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        public static final String TAG = "BroadcastReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
            if (DownloadService.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                fileData.clear();
                String jsonData = intent.getStringExtra(DownloadService.DOWNLOADED_DATA);
                if (jsonData != null && !jsonData.isEmpty()) {
                    fileData = parseJsonData(jsonData);
                    Log.d(TAG, fileData.toString());
                    adp = new ArrayAdapter<>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, fileData);
                    lV.setAdapter(adp);
                }
            }
        }
    };

}