package com.dalimao.mytaxi.common.biz;

/**
 * author: apple
 * created on: 2018/5/15 下午6:14
 * description:返回业务数据的公共格式
 */
public class BaseBizResponse
{
    public static final int STATE_OK = 200;
    public static final int STATE_USER_EXIST = 100003;
    public static final int STATE_USER_NOT_EXIT = 100002;
    public static final int STATE_PW_ERROR = 100005;

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
