package com.dalimao.mytaxi.account.view;

/**
 * author: apple
 * created on: 2018/5/24 上午11:54
 * description:
 */
public interface IDialogView {
    /**
     * 显示 Loading
     */
    void showLoading();
    /**
     * 显示错误提示
     * @param Code
     * @param msg
     */
    void showError(int Code, String msg);


}
