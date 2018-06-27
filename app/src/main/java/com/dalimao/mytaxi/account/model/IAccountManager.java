package com.dalimao.mytaxi.account.model;

import android.os.Handler;

/**
 * author: apple
 * created on: 2018/5/24 上午11:35
 * description:账号相关业务逻辑抽象
 */
public interface IAccountManager {
    //服务器错误
    int SERVER_FAIL = -999;
    //验证码发送成功
    int SMS_SEND_SUC = 1;
    //验证码发送失败
    int SMS_SEND_FAIL = -1;
    //验证码校验成功
    int SMS_CHECK_SUC = 2;
    //验证码错误
    int SMS_CHECK_FAIL = -2;
    //用户已经存在
    int USER_EXIST = 3;
    //用户不存在
    int USER_NOT_EXIST = -3;
    //注册成功
    int REGISTER_SIUC = 4;
    //登录成功
    int LOGIN_SUC = 5;

    //登录失效
    int TOKEN_INVALID = -6;
    //密码错误
    int PASSWORD_ERROR = -7;

    void setHandler(Handler handler);
    /**
     * 下发验证码
     */
    void fetchSMSCode(String phone);
    /**
     * 校验验证码
     */
    void checkSmsCode(String phone, String smsCode);
    /**
     * 用户是否注册
     */
    void checkUserExist(String phone);
    /**
     * 注册
     */
    void register(String phone, String password);
    /**
     * 登录
     */
    void login(String phone, String password);
    /**
     * token 登录
     */
    void loginByToken();


}
