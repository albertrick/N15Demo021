package com.example.n15demo021;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends Service {
    public static final String ACTION_DOWNLOAD_COMPLETE = "com.example.n15demo021.DOWNLOAD_COMPLETE";
    public static final String DOWNLOADED_DATA = "downloadedData";
    public static final String TAG = "DownloadService";
    private String urlString;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        urlString = "https://boi.org.il/currency.xml";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        new Thread(() -> {
            String downloadedData = "";
            try {
                Log.i(TAG, "Thread started");
                URL urlObject = new URL(urlString);
                URLConnection connection = urlObject.openConnection();
                InputStream inputStream = connection.getInputStream();
                StringBuilder sb = new StringBuilder();
                int data;
                while ((data = inputStream.read()) != -1) {
                    sb.append((char) data);
                }
                downloadedData = sb.toString();
                Log.d(TAG, downloadedData);
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error downloading data: " + e.getMessage());
            }

            Intent broadcastIntent = new Intent(ACTION_DOWNLOAD_COMPLETE);
            broadcastIntent.putExtra(DOWNLOADED_DATA, downloadedData);
            sendBroadcast(broadcastIntent);

            stopSelf();
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}