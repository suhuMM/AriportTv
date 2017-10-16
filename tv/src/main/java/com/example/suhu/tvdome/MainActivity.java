package com.example.suhu.tvdome;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.suhu.tvdome.server.MapPointServices;


public class MainActivity extends Activity {

    private TouchImageView imageView;
    private MapPointServices services;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageView = new TouchImageView(this);
        setContentView(imageView);

//        setContentView(R.layout.activity_main);
//        imageView = findViewById(R.id.image);
        imageView.setFocusable(true);
        bindServices();
    }

    private void bindServices() {
        Intent intent = new Intent(this, MapPointServices.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MapPointServices.SerViceBinder binder = (MapPointServices.SerViceBinder) service;
            services = binder.getService();
            if (imageView != null) {
                services.setImageView(imageView);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            services = null;
        }
    };
}
