package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.util.MyLoger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

import static org.junit.Assert.*;

/**
 * Created by liulan on 2018/5/12.
 */
public class OkHttpClientImplTest {
    IHttpClient httpClient;
    @Before
    public void setUp() throws Exception {
        httpClient = new OkHttpClientImpl();
        API.Config.setDebug(false);
    }

    @Test
    public void get() throws Exception {
        //request 对象
        String url= API.Config.getDomain()+API.TEST_GET;
        IRequest request= new BaseRequest(url);
//        request.setBody("uid","123");
        request.setBody("uid","lanlan");
        request.setHeader("testheader","test header");
       IResponse response =  httpClient.get(request,false);
        MyLoger.system("code:"+response.getCode());
        MyLoger.system("data:"+response.getData());
    }

    @Test
    public void post() throws Exception {
        String url= API.Config.getDomain()+API.TEST_POST;git
        IRequest request = new BaseRequest(url);
        request.setBody("name","lanlan");
        request.setBody("passowrd","123");
        request.setHeader("testheader","test header");
        IResponse response= httpClient.post(request,false);
        MyLoger.system("code:"+response.getCode());
        MyLoger.system("data:"+response.getData());
    }

}