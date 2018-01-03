package com.example.suhu.tvdome.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.GridLayoutManager;
import android.widget.Toast;

import com.example.suhu.tvdome.MapFragmentDataBind;
import com.example.suhu.tvdome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suhu
 * @data 2017/10/31.
 * @description:mac编辑
 */

public class MapActivity extends Activity {
    private MapFragmentDataBind dataBind;
    private MapPointServices services;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MapPointServices.SerViceBinder binder = (MapPointServices.SerViceBinder) service;
            services = binder.getService();
            services.setImageView(dataBind.mapView);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            services = null;
        }
    };
    private MapMessageAdapter adapter;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBind = DataBindingUtil.setContentView(this, R.layout.fragmnet_show_map);
        bindServices();
        recyclerViewData();
    }

    private void recyclerViewData() {
        list = new ArrayList<>();
        list.add("消防车1");
        list.add("装甲车2");
        list.add("消防车3");
        list.add("消防车4");
        list.add("消防车5");
        list.add("装甲车6");
        list.add("消防车7");
        list.add("消防车8");
        list.add("消防车9");
        list.add("装甲车10");
        dataBind.recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        dataBind.recyclerView.addItemDecoration(new SpaceItemDecoration(15));
        adapter = new MapMessageAdapter(this, list);
        dataBind.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MapMessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MapActivity.this, list.get(position), Toast.LENGTH_SHORT).show();
            }
        });

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
