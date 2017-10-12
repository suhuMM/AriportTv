package com.example.phone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.phone.server.MapPointServices;

public class MainActivity extends AppCompatActivity {

    private TouchImageView imageView;
    private MapPointServices services;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getSupportActionBar().hide();
        imageView = new TouchImageView(this);
        setContentView(imageView);
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
