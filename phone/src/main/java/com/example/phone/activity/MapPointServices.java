package com.example.phone.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.example.phone.Latitude;
import com.example.phone.http.ApiRequestFactory;
import com.example.phone.http.ApiRequestMethods;
import com.example.phone.http.ApiUrl;
import com.example.phone.http.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
    private MapMessageAdapter adapter;


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
                List<ItemMessage> itemList = new ArrayList<>();
                JSONObject object = null;
                try {
                    object = new JSONObject(response).getJSONObject("data");
                    int flag = object.getInt("flag");
                    switch (flag){
                        case -3:
                            ToastUtils.disPlayShort(MapPointServices.this, "参数为空");
                            return;
                        case -5:
                            ToastUtils.disPlayShort(MapPointServices.this, "无数据");
                            List<Latitude> list =new ArrayList<>();
                            if (imageView != null) {
                                imageView.drawPoint(list);
                            }
                            if (adapter!=null){
                                adapter.setList(itemList);
                            }
                            return;
                        case 0:
                            if (!object.has("data")){
                                return;
                            }
                        default:
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                info = new Gson().fromJson(object.toString(), Information.class);
                List<Information.DataBean> dataList = new ArrayList<>();
                if (info != null) {
                    dataList = info.getData();
                }

                List<Latitude> list = new ArrayList<>();

                for (Information.DataBean dataBean : dataList) {

                    String longLatitude = dataBean.getLongitudeLatitude();
                    String[] ss = longLatitude.split(",");
                    try {
                        if (Double.parseDouble(ss[0])>0&&Double.parseDouble(ss[1])>0){
                            list.add(new Latitude(Double.parseDouble(ss[0]), Double.parseDouble(ss[1])));
                            ItemMessage message = new ItemMessage(dataBean.getUuid(),0);
                            itemList.add(message);
                        }

                    } catch (Exception e) {
                    }
                }

                if (imageView != null) {
                    if (list.size()==0){
                        ToastUtils.disPlayShort(MapPointServices.this, "无可用数据");
                    }
                    imageView.drawPoint(list);
                }
                if (adapter!=null){
                    adapter.setList(itemList);
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

    public void setAdapter(MapMessageAdapter adapter) {
        this.adapter = adapter;
    }

    public class SerViceBinder extends Binder {
        public MapPointServices getService() {
            return MapPointServices.this;
        }
    }



}
