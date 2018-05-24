package com.dalimao.mytaxi.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.account.model.response.Login;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.google.gson.Gson;

/**
 * author: apple
 * created on: 2018/5/15 上午9:52
 * description:
 * 1 检查本地记录（登录状态检查）
 * 2 若用户没有登陆则登陆
 * 3 登陆之前先校验手机号
 * 4 token 有效使用 token 自动登录
 * todo 地图初始化
 */
public class MainActivity extends AppCompatActivity{
    OkHttpClientImpl mHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHttpClient = new OkHttpClientImpl();
        checkLoginState();
    }

    private void checkLoginState() {
        //  获取本地登陆信息
        SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);
           Login login = (Login) dao.get(SharedPreferencesDao.KEY_ACCOUNT,Login.class);
        
        //登陆是否过期
        boolean tokenValid = false;

        // 检查 token 是否过期
        if (login!=null){
            if (login.getExpired() > System.currentTimeMillis()){
                tokenValid = true;
            }
        }
        if (!tokenValid){
            showPhoneInputDialog();
        }else{
            final String token =login.getToken();
            // 请求网络，完成自动登陆

        }
    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        PhoneInputDialog dialog= new PhoneInputDialog(this);
        dialog.show();
    }


}
