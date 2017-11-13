package com.example.phone.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Window;
import android.view.WindowManager;

import com.example.phone.MapFragmentDataBind;
import com.example.phone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhu
 * @data 2017/10/31.
 * @description
 */

public class MapActivity extends AppCompatActivity {
    private MapFragmentDataBind dataBind;
    private MapPointServices services;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MapPointServices.SerViceBinder binder = (MapPointServices.SerViceBinder) service;
            services = binder.getService();
            services.setImageView(dataBind.mapView);
            services.setAdapter(adapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            services = null;
        }
    };
    private MapMessageAdapter adapter;
    private List<ItemMessage> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getSupportActionBar().hide();
        dataBind = DataBindingUtil.setContentView(this, R.layout.fragmnet_show_map);
        recyclerViewData();
        bindServices();
    }

    private void recyclerViewData() {
        list = new ArrayList<>();
        dataBind.recyclerView.setLayoutManager(new GridLayoutManager(this, 10));
        dataBind.recyclerView.addItemDecoration(new SpaceItemDecoration(15));
        adapter = new MapMessageAdapter(this, list);
        dataBind.recyclerView.setAdapter(adapter);


    }

    private void bindServices() {
        Intent intent = new Intent(this, MapPointServices.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
