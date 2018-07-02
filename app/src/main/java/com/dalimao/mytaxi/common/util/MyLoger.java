package com.dalimao.mytaxi.common.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    public static void d(String Tag,String msg){
        if (IsDebug){
            Log.d(Tag,msg);
        }
    }
    public static void toast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
