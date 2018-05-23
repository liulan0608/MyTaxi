package com.dalimao.mytaxi.common.storage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.dalimao.mytaxi.common.util.MyLoger;
import com.google.gson.Gson;

/**
 * author: apple
 * created on: 2018/5/23 下午12:08
 * description:
 */
public class SharedPreferencesDao {
    private static  final String TAG = "SharedPreferencesDao";
    public static  final String FILE_ACCOUNT = "FILE_ACCOUNT";
    public static  final String KEY_ACCOUNT = "KEY_ACCOUNT";

    private SharedPreferences sharedPreferences;
    public SharedPreferencesDao(Application application,String fileName) {
        sharedPreferences = application.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 保存 k-v
     * @param key
     * @param value
     */
    public void save(String key,String value) {
        sharedPreferences.edit().putString(key, value);
    }

    /**
     * 读取 k
     * @param key
     * @return
     */

    public String get(String key){
        return  sharedPreferences.getString(key,"");
    }
    public void save(String key,Object ob) {
        String value = new Gson().toJson(ob);
        save(key,value);
    }
    public Object get(String key,Class cls){
        String value = get(key);
        try {
            if (value != null){
                Object o = new Gson().fromJson(value,cls);
                return o;
            }
        }catch (Exception e){
            MyLoger.dd(TAG,e.getMessage());
        }
        return  null;
    }
}
