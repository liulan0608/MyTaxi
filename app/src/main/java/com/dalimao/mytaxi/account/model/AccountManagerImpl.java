package com.dalimao.mytaxi.account.model;

import android.content.Context;
import android.os.Handler;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.account.model.response.Login;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.DevUtil;
import com.google.gson.Gson;

import rx.functions.Func1;

/**
 * author: apple
 * created on: 2018/5/24 下午12:00
 * description:
 */
public class AccountManagerImpl implements IAccountManager{
    private static final String TAG = "AccountManagerImpl";
    //网络请求库
    private IHttpClient httpClient;
    //数据存储
    private SharedPreferencesDao sharedPreferencesDao;
    //发送消息 handler
    private Handler handler;
    //上下文
    private Context mContext;

    public AccountManagerImpl(IHttpClient httpClient, SharedPreferencesDao sharedPreferencesDao) {
        this.httpClient = httpClient;
        this.sharedPreferencesDao = sharedPreferencesDao;
        mContext = MyTaxiApplication.getInstance();
    } public AccountManagerImpl( SharedPreferencesDao sharedPreferencesDao) {
        this.httpClient = new OkHttpClientImpl();;
        this.sharedPreferencesDao = sharedPreferencesDao;
        mContext = MyTaxiApplication.getInstance();
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 获取验证码
     * @param phone
     */
    @Override
    public void fetchSMSCode(final String phone) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                IResponse response = httpClient.get(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson()
                            .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK){
//                        handler.sendEmptyMessage(SMS_SEND_SUC);
                    }else{
                        handler.sendEmptyMessage(SMS_SEND_FAIL);
                    }
                }else{
                    handler.sendEmptyMessage(SMS_SEND_FAIL);
                }
                return null;
            }
        });

            }

    /**
     * 校验验证码
     * @param phone
     * @param smsCode
     */
    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        new Thread(){
            @Override
            public void run() {
                String url = API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("code",smsCode);
                IResponse response = httpClient.get(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson()
                            .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                        handler.sendEmptyMessage(SMS_CHECK_SUC);
                    }else{
                        handler.sendEmptyMessage(SMS_CHECK_FAIL);
                    }
                }else{
                    handler.sendEmptyMessage(SMS_CHECK_FAIL);
                }
            }
        }.start();
    }

    /**
     * 检验用户是否存在
     * @param phone
     */
    @Override
    public void checkUserExist(final String phone) {
        new Thread(){
            @Override
            public void run() {
                String url = API.CHECK_USER_EXIST;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                IResponse response = httpClient.get(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson()
                            .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST){
                        handler.sendEmptyMessage(USER_EXIST);
                    }else if (bizRes.getCode() == BaseBizResponse.STATE_USER_NOT_EXIT){
                        handler.sendEmptyMessage(USER_NOT_EXIST);
                    }
                }else{
                    handler.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
    }

    /**
     * 注册
     * @param phone
     * @param password
     */
    @Override
    public void register(final String phone, final String password) {
        new Thread(){
            @Override
            public void run() {
                String url= API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("password",password);
                request.setBody("uid", DevUtil.UUID(mContext));
                IResponse response = httpClient.post(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                    BaseBizResponse bizREs = new Gson().fromJson(response.getData(),BaseBizResponse.class);
                    if (bizREs.getCode() == BaseBizResponse.STATE_OK){
                        handler.sendEmptyMessage(REGISTER_SIUC);
                    }else {
                        handler.sendEmptyMessage(SERVER_FAIL);
                    }
                }else {
                    handler.sendEmptyMessage(SERVER_FAIL);
                }

            }
        }.start();
    }

    /**
     * 账号密码登录
     * @param phone
     * @param password
     */
    @Override
    public void login(final String phone, final String password) {
        new Thread(){
            @Override
            public void run() {
                String url= API.LOGIN_PHONE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("password",password);
                IResponse response = httpClient.post(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                    LoginResponse loginRes = new Gson().fromJson(response.getData(),LoginResponse.class);
                    if (loginRes.getCode() == BaseBizResponse.STATE_OK){
                        //保存登陆信息
                        Login login = loginRes.getData();
                        SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance()
                                ,SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT,login);
                        handler.sendEmptyMessage(LOGIN_SUC);
                    }else if (loginRes.getCode() == BaseBizResponse.STATE_PW_ERROR){
                        handler.sendEmptyMessage(PASSWORD_ERROR);
                    }
                }else {
                    handler.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
    }

    /**
     * token 登录
     */
    @Override
    public void loginByToken() {
        SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);
        final Login account = (Login) dao.get(SharedPreferencesDao.KEY_ACCOUNT,Login.class);
        //登陆是否过期
        boolean tokenValid = false;

        // 检查 token 是否过期
        if (account!=null){
            if (account.getExpired() > System.currentTimeMillis()){
                tokenValid = true;
            }
        }
        if (!tokenValid){
           handler.sendEmptyMessage(TOKEN_INVALID);
        }else{
            final String token =account.getToken();
            // 请求网络，完成自动登陆
            new Thread(){
                @Override
                public void run() {
                    String url= API.LOGIN_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token",account.getToken());
                    IResponse response = httpClient.post(request,false);
                    if (response.getCode() == BaseResponse.STATE_OK){
                        LoginResponse loginRes = new Gson().fromJson(response.getData(),LoginResponse.class);
                        if (loginRes.getCode() == BaseBizResponse.STATE_OK){
                            //保存登陆信息
                            Login login = loginRes.getData();
                            // TODO: 2018/5/23 加密存储 登录信息比较敏感
                            SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance()
                                    ,SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT,login);
                            handler.sendEmptyMessage(LOGIN_SUC);
                        }else if (loginRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID){
                           handler.sendEmptyMessage(TOKEN_INVALID);
                        }
                    }else {
                        handler.sendEmptyMessage(SERVER_FAIL);
                    }
                }
            }.start();
        }
    }
}
