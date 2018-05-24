package com.dalimao.mytaxi.account.view;

/**
 * author: apple
 * created on: 2018/5/24 上午11:49
 * description:
 */
public interface ISmsCodeDialogView extends IDialogView{

    /**
     * 显示倒计时
     */
    void showCountDownTimer();
    /**
     * 验证码状态
     */
    void showSmsCodeCheckState(boolean b);
    /**
     * 检验用户是否存在
     */
    void showUserExist(boolean b);
}
