package com.example.suhu.tvdome.http;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suhu on 2017/4/8.
 */

public class ApiRequestMethods {

    /**
     *@method 请求方法
     *@author suhu
     *@time 2017/4/8 15:09
     *
    */
    public static void getInfoList(Context context, String url, int page, final ApiRequestFactory.HttpCallBackListener httpCallBackListener){
        Map<String ,String> map = new HashMap<>();
        map.put("page",page+"");
        ApiRequestFactory.postJson(context,url,map,httpCallBackListener,false);
    }



}
