package com.dalimao.mytaxi.account.pressenter;

/**
 * author: apple
 * created on: 2018/5/24 上午11:40
 * description:
 */
public interface ISmsCodeDialogPresenter {
    /**
     * 请求下发验证码
     */
    void requestSendSmsCode(String phone);
    /**
     * 请求校验验证码
     */
    void requestCheckSmsCode(String phone , String smsCode);
    /**
     * 用户是否存在
     */
    void requestCheckUserExist(String phone);

}
