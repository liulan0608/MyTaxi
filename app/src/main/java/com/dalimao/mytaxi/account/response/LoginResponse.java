package com.dalimao.mytaxi.account.response;

import com.dalimao.mytaxi.account.bean.Login;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;

/**
 * author: apple
 * created on: 2018/5/23 下午12:04
 * description:
 */
public class LoginResponse extends BaseBizResponse{
    Login data;

    public Login getData() {
        return data;
    }

    public void setData(Login data) {
        this.data = data;
    }
}
