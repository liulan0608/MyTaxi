package com.dalimao.mytaxi.util;

import android.util.Log;

/**
 * Created by liulan on 2018/5/12.
 */

public class MyLoger {
    private static  String TAG="imooc_lanlan";
    private static  boolean IsDebug=true;

    public void setDebug(boolean debug) {
        IsDebug = debug;
    }

    public static  void system(String msg){
        if (IsDebug){
        System.out.println(msg);
        }
    }
    public static void d(String msg){
        if (IsDebug){
            Log.d(TAG,msg);
        }
    }
}
