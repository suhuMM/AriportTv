package com.example.suhu.tvdome.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.example.suhu.tvdome.Latitude;
import com.example.suhu.tvdome.http.ApiRequestFactory;
import com.example.suhu.tvdome.http.ApiRequestMethods;
import com.example.suhu.tvdome.http.ApiUrl;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * @author suhu
 * @data 2017/10/12.
 * @description
 */

public class MapPointServices extends Service {
    private AirPortMapView imageView;
    private Information info;
    private boolean tag = true;
    private Handler handler;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        requestData();
        return new SerViceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        tag = false;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                getInfoList();
            }
        };


    }

    private void requestData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (tag){
                    try {
                        handler.sendEmptyMessage(0);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    private void getInfoList() {
        ApiRequestMethods.getInfoList(MapPointServices.this, ApiUrl.LIST,0, new ApiRequestFactory.HttpCallBackListener() {
            @Override
            public void onSuccess(String response, String url, int id) {

                info = new Gson().fromJson(response, Information.class);

                if (info == null) {
                    return;
                }
                List<Information.DataBean> dataList = info.getData();
                List<Latitude> list = new ArrayList<>();

                for (Information.DataBean dataBean : dataList) {
                    String longLatitude = dataBean.getLongitudeLatitude();
                    String[] ss = longLatitude.split(",");
                    try {
                        list.add(new Latitude(Double.parseDouble(ss[0]), Double.parseDouble(ss[1])));
                    } catch (Exception e) {
                    }
                }

                list.add(new Latitude(116.584559, 39.785231));
                list.add(new Latitude(116.570042, 39.792439));
                list.add(new Latitude(116.546385, 39.77919));
                list.add(new Latitude(116.636153, 39.802102));

                if (imageView != null) {
                    imageView.drawPoint(list);
                }


            }

            @Override
            public void failure(Call call, Exception e, int id) {

            }
        });
    }


    public void setImageView(AirPortMapView imageView) {
        this.imageView = imageView;
    }

    public class SerViceBinder extends Binder {
        public MapPointServices getService() {
            return MapPointServices.this;
        }
    }


}
