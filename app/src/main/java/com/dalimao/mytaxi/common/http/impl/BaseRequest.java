package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liulan on 2018/5/12.
 */

public class BaseRequest implements IRequest{
    private String method=POST;
    private String url;
    Map<String,String> header;
    Map<String,String> body;

    public BaseRequest(String url) {
        /**
         * 公共参数及头部信息
         */
        this.url = url;
        header=new HashMap<>();
        body=new HashMap<>();
        header.put("Application-Id","myTaxiId");
        header.put("API-Key","myTaxiKey");
    }

    @Override
    public void setMethod(String method) {
        this.method=method;
    }

    @Override
    public void setHeader(String key, String value) {
        header.put(key,value);
    }

    @Override
    public void setBody(String key, String value) {
        body.put(key,value);
    }

    @Override
    public String getUrl() {
        if (GET.equals(method)){
            //组装 get 请求参数
            for (String key : body.keySet()){
                url=url.replace("${" + key + "}" , body.get(key).toString());
            }
        }
        return url;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public String getBody() {
        if (body!=null){
            //组装 post 方法请求参数
            return  new Gson().toJson(body, HashMap.class);
        }else {
            return "{}";
        }
    }
}
