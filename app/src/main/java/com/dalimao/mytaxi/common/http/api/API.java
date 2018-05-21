package com.dalimao.mytaxi.common.http.api;

/**
 * Created by liulan on 2018/5/12.
 */

public class API {
    public static final String TEST_GET = "/get?uid=${uid}";
    public static final String TEST_POST = "/post";

    //获取验证码
    public static final String GET_SMS_CODE = Config.getDomain()+"/f34e28da5816433d/getMsgCode?phone=${phone}";
    //校验验证码
    public static final String CHECK_SMS_CODE = Config.getDomain()+"/f34e28da5816433d/checkMsgCode?phone=${phone}&code=${code}";
    //手机是否注册
    public static  final String CHECK_USER_EXIST = Config.getDomain()+"/f34e28da5816433d/isUserExist?phone=${phone}";
    //用户注册
    public static  final String REGISTER = Config.getDomain()+"";
    //账号登录
    public static  final String LOGIN_PHONE = Config.getDomain()+"";
    //自动登录（token登录）
    public static  final String LOGIN_TOKEN = Config.getDomain()+"";
    //司机位置变化
    public static  final String DRIVER_POSITION_CHANGE = Config.getDomain()+"";


    public static class Config{
        private static final String TEST_DOMAIN = "http://cloud.bmob.cn";
        private static final String TEST_APP_ID = "e90928398db0130b0d6d21da7bde357e";
        private static final String TEST_APP_KEY = "514d8f8a2371bdf1566033f6664a24d2";

        private static final String RELEASE_DOMAIN = "http://cloud.bmob.cn";
        private static final String RELEASE_APP_ID = "e90928398db0130b0d6d21da7bde357e";
        private static final String RELEASE_APP_KEY = "514d8f8a2371bdf1566033f6664a24d2";

        private static String appId = TEST_APP_ID;
        private static String appKey = TEST_APP_KEY;
        private  static  String domain=TEST_DOMAIN;

        public static void setDebug(boolean debug){
            domain=debug ? TEST_DOMAIN : RELEASE_DOMAIN;
            appId=debug ? TEST_APP_KEY : RELEASE_APP_ID;
            appKey=debug ? TEST_APP_KEY : RELEASE_APP_KEY;

        }
        public static String getDomain(){
            return  domain;
        }

        public static String getAppId() {
            return appId;
        }

        public static String getAppKey() {
            return appKey;
        }
    }
}

