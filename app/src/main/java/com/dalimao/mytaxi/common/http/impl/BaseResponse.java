package com.dalimao.mytaxi.common.http.impl;

import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;

/**
 * Created by liulan on 2018/5/12.
 */

public class BaseResponse implements IResponse{
    public static  final int STATE_UNKNOWN = 100001;
    public static final int STATE_OK = 200;
    //状态吗
    private int code;
    //响应数据
    private String data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getData() {
        return data;
    }
}
