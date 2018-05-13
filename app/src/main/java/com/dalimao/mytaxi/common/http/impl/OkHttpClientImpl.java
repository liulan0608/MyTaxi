package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liulan on 2018/5/12.
 */

public class OkHttpClientImpl implements IHttpClient {
    OkHttpClient client=new OkHttpClient.Builder().build();
    @Override
    public IResponse get(IRequest request, boolean forceCache) {
        request.setMethod(IRequest.GET);
        //解析头部
        Map<String,String> header = request.getHeader();
        Request.Builder builder = new Request.Builder();
        for (String key : header.keySet()){
            builder.header(key,header.get(key));
        }
        builder.url(request.getUrl()).get();
        Request okRequest = builder.build();
        return excute(okRequest);
    }



    @Override
    public IResponse post(IRequest request, boolean forceCache) {
        MediaType mediaType= MediaType.parse("application/json; charset=utf-8");
        request.setMethod(IRequest.POST);
        RequestBody body = RequestBody.create(mediaType,request.getBody().toString());
        Map<String ,String>  header = request.getHeader();
        Request.Builder builder= new Request.Builder();
        for (String key : header.keySet()){
           builder.header(key, header.get(key));
        }
        builder.url(request.getUrl())
                .post(body);
        Request okRequest = builder.build();
        return excute(okRequest);
    }
    private IResponse excute(Request okRequest) {
        BaseResponse commonResponse = new BaseResponse();
        try {
            Response response = client.newCall(okRequest).execute();
            commonResponse.setCode(response.code());
            String body=response.body().string();
            commonResponse.setData(body);
        } catch (IOException e) {
            e.printStackTrace();
            commonResponse.setCode(commonResponse.STATE_UNKNOWN);
            commonResponse.setData(e.getMessage());
        }
        return commonResponse;
    }
}
