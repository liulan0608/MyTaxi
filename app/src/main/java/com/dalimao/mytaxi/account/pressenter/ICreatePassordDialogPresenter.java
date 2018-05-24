package com.dalimao.mytaxi.account.pressenter;

/**
 * author: apple
 * created on: 2018/5/24 上午11:40
 * description:
 */
public interface ICreatePassordDialogPresenter {
    /**
     * 检验密码输入合法性
     */
    void checkPw(String pw, String pw1);
    /**
     * 提交注册
     */
    void requestRegister(String phone, String pw);
    /**
     * 登录
     */
    void requestLogin(String phone, String pw);

}
