package com.dalimao.mytaxi.account.bean;

/**
 * author: apple
 * created on: 2018/5/23 下午12:02
 * description:
 */
public class Login {
    private String uid;
    private String expired;
    private String account;
    private String token;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
