package com.dalimao.mytaxi;

import android.app.Application;

/**
 * author: apple
 * created on: 2018/5/23 下午3:16
 * description:
 */
public class MyTaxiApplication extends Application{
    private static  MyTaxiApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance =this;

    }
    public static MyTaxiApplication getInstance (){
        return instance;
    }
}
