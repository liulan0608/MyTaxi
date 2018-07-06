/*本类是SharedPreferences的工具封装类，主要给程序保存提取简单的设置数据信息*/

package com.dalimao.mytaxi.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SaveData_withPreferences extends Activity {
	Context context;
	public static String KEY_ACCOUNT = "Account";

	public SaveData_withPreferences(Context context) {
		this.context = context;
	}

	public void saveDatas(String key, int value) {
		SharedPreferences uiState = context
				.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = uiState.edit();

		editor.putInt(key, value);

		editor.commit();
	}

	public void saveDatas_Boolean(String key, Boolean value) {
		SharedPreferences uiState = context
				.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = uiState.edit();

		editor.putBoolean(key, value);

		editor.commit();
	}

	public void saveDatas_String(String key, String value) {
		SharedPreferences uiState = context
				.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = uiState.edit();

		editor.putString(key, value);

		editor.commit();
	}

	public void saveDatas_long(String key, Long value) {
		SharedPreferences uiState = context
				.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = uiState.edit();

		editor.putLong(key, value);

		editor.commit();
	}
	
	public void saveDatas_int(String key, int value) {
		SharedPreferences uiState = context
				.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = uiState.edit();

		editor.putInt(key, value);

		editor.commit();
	}

	public void saveDatas_object(String key,Object ob) {
		String value = new Gson().toJson(ob);

		saveDatas_String(key,value);
	}
	public Object getData_object(String key,Class cls){
		String value = getData_String(key);
		try {
			if (value != null){
				Object o = new Gson().fromJson(value,cls);
				return o;
			}
		}catch (Exception e){
			MyLoger.d("sharedPreferences",e.getMessage());
		}
		return  null;
	}
	public Long getData_long(String key) {
		SharedPreferences share = context.getSharedPreferences("user_info", 0);

		return share.getLong(key, Long.parseLong("10000000"));
	}

	public int getData_int(String key) {
		SharedPreferences share = context.getSharedPreferences("user_info", 0);

		return share.getInt(key, 0);
	}

	public Boolean getData_Boolean(String key) {
		
		SharedPreferences share = context.getSharedPreferences("user_info", 0);
		return share.getBoolean(key, true);
        	
	}
	
	public Boolean getData_BBoolean(String key) {
		SharedPreferences share = context.getSharedPreferences("user_info", 0);
		return share.getBoolean(key, false);
	}
	
	public Boolean getData_Bool(String key) {
		SharedPreferences share = context.getSharedPreferences("user_info", 0);
		return share.getBoolean(key, false);
	}
	
	public void saveData_Bool(String key, Boolean value) {
		SharedPreferences uiState = context
				.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = uiState.edit();

		editor.putBoolean(key, value);

		editor.commit();
	}

	public String getData_String(String key) {
		SharedPreferences share = context.getSharedPreferences("user_info", 0);
		return share.getString(key, "0");
	}
	
	
	public Boolean getWelcomeWeb_Boolean(String key) {
		
		SharedPreferences share = context.getSharedPreferences("user_info", 0);
		return share.getBoolean(key, false);
        	
	}
	
	public void saveWelcomeWeb_Boolean(String key, Boolean value) {
		SharedPreferences uiState = context
				.getSharedPreferences("user_info", 0);
		SharedPreferences.Editor editor = uiState.edit();

		editor.putBoolean(key, value);

		editor.commit();
	}
}
