package com.dalimao.mytaxi;

import com.dalimao.mytaxi.util.MyLoger;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class OkHttpTest {
    /**
     * 测试 get 方法
     */
    @Test
    public void get() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        try {
            Response response = client.newCall(request).execute();
            MyLoger.system(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 post 方法
     */@Test
    public void post(){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType=MediaType.parse("application/json; charset=utf-8");
        RequestBody body=RequestBody.create(mediaType,"{\"name\" : \"lanlan\"}");

        Request request = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            MyLoger.system(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试拦截
     */
    @Test
    public void interceptor(){
        Interceptor interceptor=new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                long start = System.currentTimeMillis();
                Request request = chain.request();
                Response response = chain.proceed(request);
                long end  = System.currentTimeMillis();
                MyLoger.system("请求时间："+ (end-start));
                return response;
            }
        } ;
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  测试缓存
     */
    @Test
    public void cache(){
        Cache cache=new Cache(new File("cache.cache"),1024*1024);
        OkHttpClient client = new OkHttpClient.Builder().cache(cache).build();
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Response responseCache = response.cacheResponse();
            Response responseNet = response.networkResponse();
            if (responseCache!=null)MyLoger.system("缓存：");
            if (responseNet!=null)MyLoger.system("网络：");
            MyLoger.system(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}