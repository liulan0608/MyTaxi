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
                BaseBizResponse bizREs = new BaseBizResponse();
                if (response.getCode() == BaseResponse.STATE_OK){
                    bizREs = new Gson()
                            .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                    if (bizREs.getCode() == BaseBizResponse.STATE_OK){
                        bizREs.setCode(SMS_SEND_SUC);
                    }else{
                        bizREs.setCode(SMS_SEND_FAIL);
                    }
                }else{
                    bizREs.setCode(SMS_SEND_FAIL);
                }
                return bizREs;
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
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                String url = API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("code",smsCode);
                IResponse response = httpClient.get(request,false);
                BaseBizResponse bizREs = new BaseBizResponse();
                if (response.getCode() == BaseResponse.STATE_OK){
                    bizREs = new Gson()
                            .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                    if (bizREs.getCode() == BaseBizResponse.STATE_OK){
                        bizREs.setCode(SMS_CHECK_SUC);
                    }else{
                        bizREs.setCode(SMS_CHECK_FAIL);
                    }
                }else{
                    bizREs.setCode(SMS_CHECK_FAIL);
            }
                return bizREs;
            }
        });
    }

    /**
     * 检验用户是否存在
     * @param phone
     */
    @Override
    public void checkUserExist(final String phone) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {


                String url = API.CHECK_USER_EXIST;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                IResponse response = httpClient.get(request,false);
                BaseBizResponse bizREs = new BaseBizResponse();
                if (response.getCode() == BaseResponse.STATE_OK){
                    bizREs = new Gson()
                            .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                    if (bizREs.getCode() == BaseBizResponse.STATE_USER_EXIST){
                        bizREs.setCode(USER_EXIST);
                    }else if (bizREs.getCode() == BaseBizResponse.STATE_USER_NOT_EXIT){
                        bizREs.setCode(USER_NOT_EXIST);
                    }
                }else{
                    bizREs.setCode(SERVER_FAIL);
                }
                return bizREs;
            }
        });
    }

    /**
     * 注册
     * @param phone
     * @param password
     */
    @Override
    public void register(final String phone, final String password) {
       RxBus.getInstance().chainProcess(new Func1() {
           @Override
           public Object call(Object o) {
                String url= API.REGISTER;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",phone);
                request.setBody("password",password);
                request.setBody("uid", DevUtil.UUID(mContext));
                IResponse response = httpClient.post(request,false);
               BaseBizResponse bizREs = new BaseBizResponse();
                if (response.getCode() == BaseResponse.STATE_OK){
                     bizREs = new Gson().fromJson(response.getData(),BaseBizResponse.class);
                    if (bizREs.getCode() == BaseBizResponse.STATE_OK){
                        bizREs.setCode(REGISTER_SIUC);
                    }else {
                        bizREs.setCode(SERVER_FAIL);
                    }
                }else {
                    bizREs.setCode(SERVER_FAIL);
                }
               return bizREs;
           }
       });
    }
    /**
     * 账号密码登录
     * @param phone
     * @param password
     */
    @Override
    public void login(final String phone, final String password) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                String url= API.LOGIN_PHONE;
                IRequest request = new BaseRequest(url);
                    request.setBody("phone",phone);
                    request.setBody("password",password);
                    IResponse response = httpClient.post(request,false);
                    LoginResponse bizRes = new LoginResponse();
                    if (response.getCode() == BaseResponse.STATE_OK){
                        bizRes = new Gson().fromJson(response.getData(),LoginResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                            //保存登陆信息
                            Login login = bizRes.getData();
                            SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance()
                                    ,SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT,login);
                            bizRes.setCode(LOGIN_SUC);
                        }else if (bizRes.getCode() == BaseBizResponse.STATE_PW_ERROR){
                            bizRes.setCode(PASSWORD_ERROR);
                        }
                    }else {
                    bizRes.setCode(SERVER_FAIL);
                }
                return bizRes;
            }
        });
    }
    /**
     * token 登录
     */
    @Override
    public void loginByToken() {
        // 请求网络，完成自动登陆
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
        SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                SharedPreferencesDao.FILE_ACCOUNT);
        final Login account = (Login) dao.get(SharedPreferencesDao.KEY_ACCOUNT,Login.class);
        //登陆是否过期
        boolean tokenValid = false;
        // 检查 token 是否过期
        if (account!=null){
            if (account.getExpired() > System.currentTimeMillis()){
                tokenValid = true;
            }
        }
        LoginResponse loginRes = new LoginResponse();
        if (!tokenValid){
            loginRes.setCode(TOKEN_INVALID);
        }else{
            final String token =account.getToken();
                    String url= API.LOGIN_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token",account.getToken());
                    IResponse response = httpClient.post(request,false);
                    if (response.getCode() == BaseResponse.STATE_OK){
                        loginRes = new Gson().fromJson(response.getData(),LoginResponse.class);
                        if (loginRes.getCode() == BaseBizResponse.STATE_OK){
                            //保存登陆信息
                            Login login = loginRes.getData();
                            // TODO: 2018/5/23 加密存储 登录信息比较敏感
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT,login);
                            loginRes.setCode(LOGIN_SUC);
                        }else if (loginRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID){
                            loginRes.setCode(TOKEN_INVALID);
                        }
                    }else {
                        loginRes.setCode(SERVER_FAIL);
                    }

        }
                return loginRes;
            }
        });
    }

}
