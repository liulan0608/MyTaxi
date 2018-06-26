package com.dalimao.mytaxi.common.util;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;

/**
 * author: apple
 * created on: 2018/6/26 下午5:54
 * description:
 */
public class Global {
    public static SharedPreferencesDao sharedPrefDao =new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);



}
