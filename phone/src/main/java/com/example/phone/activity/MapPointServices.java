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
                            ItemMessage message = new ItemMessage(dataBean.getUuid(),2);
                            itemList.add(message);
                        }

                    } catch (Exception e) {
                    }
                }

                if (imageView != null) {
                    if (list.size()==0){
                        ToastUtils.disPlayShort(MapPointServices.this, "无可用数据");
                    }
                    setTest();
                    //imageView.drawPoint(list);
                }
                if (adapter!=null){
                    itemList.add(new ItemMessage("test",0));
                    itemList.add(new ItemMessage("test",1));
                    itemList.add(new ItemMessage("test",2));
                    itemList.add(new ItemMessage("test",0));
                    itemList.add(new ItemMessage("test",1));
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

    private void setTest(){
        List<Latitude> latitudes = new ArrayList<>();
        //B
        latitudes.add(new Latitude(116.124648,37.500543));
        //M
        latitudes.add(new Latitude(116.107680,37.480831));
        //N
        latitudes.add(new Latitude(116.107058,37.480695));
        //J
        latitudes.add(new Latitude(116.111086,37.481456));
        //K
        latitudes.add(new Latitude(116.109463,37.482501));
        //D
        latitudes.add(new Latitude(116.126493,37.501876));
        //E
        latitudes.add(new Latitude(116.125886,37.502158));
        //A
        latitudes.add(new Latitude(116.109060,37.482765));
        //C
        latitudes.add(new Latitude(116.107071,37.48063));
        //F
        latitudes.add(new Latitude(116.126521,37.498979));
        //H
        latitudes.add(new Latitude(116.118393,37.490060));
        //I
        latitudes.add(new Latitude(116.117028,37.491093));
        //下方
        latitudes.add(new Latitude(116.124456,37.495076));


        imageView.drawPoint(latitudes);
    }

}
