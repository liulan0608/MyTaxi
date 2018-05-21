package com.dalimao.mytaxi.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.PhoneInputDialog;

/**
 * author: apple
 * created on: 2018/5/15 上午9:52
 * description:
 * 1 检查本地记录（有没有登陆）
 * 2 若用户没有登陆则登陆
 * 3 登陆之前先校验手机号
 * todo 地图初始化
 */
public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLoginState();

    }

    private void checkLoginState() {
        // TODO: 获取本地登陆信息 
        
        //登陆是否过期
        boolean tokenValid = false;

        // TODO: 检查 token 是否过期 
        
        if (!tokenValid){
            showPhoneInputDialog();
        }else{
            // TODO: 2018/5/15  请求网络，完成自动登陆
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
