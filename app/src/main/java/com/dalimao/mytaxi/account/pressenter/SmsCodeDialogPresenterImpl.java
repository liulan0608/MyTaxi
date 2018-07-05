package com.dalimao.mytaxi.account.pressenter;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.view.ISmsCodeDialogView;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.util.Global;

/**
 * author: apple
 * created on: 2018/5/24 下午2:48
 * description:
 */
public class SmsCodeDialogPresenterImpl implements ISmsCodeDialogPresenter{
    private ISmsCodeDialogView view;
    private IAccountManager accountManager;

    @RegisterBus
    public void onSmsCodeResponse(BaseBizResponse response){
        switch (response.getCode()) {
            case IAccountManager.SMS_SEND_SUC:
                //下发验证码成功
                view.showCountDownTimer();
                break;
            case IAccountManager.SMS_SEND_FAIL:
                //验证码发送失败
                view.showError(IAccountManager.SMS_SEND_FAIL, "");
                break;
            case IAccountManager.SMS_CHECK_SUC:
                //检验验证码正确
                view.showSmsCodeCheckState(true);
                break;
            case IAccountManager.SMS_CHECK_FAIL:
                view.showSmsCodeCheckState(false);
                view.showError(IAccountManager.SMS_CHECK_FAIL, "");
                break;
            case IAccountManager.USER_EXIST:
                view.showUserExist(true);
                break;
            case IAccountManager.USER_NOT_EXIST:
                view.showUserExist(false);
                break;
        }
    }

    public SmsCodeDialogPresenterImpl(ISmsCodeDialogView view) {
        this.view = view;
        this.accountManager = new AccountManagerImpl(Global.sharedPrefDao);
    }

    /**
     * 请求获取验证码
     * @param phone
     */
    @Override
    public void requestSendSmsCode(String phone) {
        accountManager.fetchSMSCode(phone);
    }

    /**
     * 请求校验验证码
     * @param phone
     * @param smsCode
     */
    @Override
    public void requestCheckSmsCode(String phone, String smsCode) {
        accountManager.checkSmsCode(phone,smsCode);
    }

    /**
     * 请求检验用户是否存在
     * @param phone
     */
    @Override
    public void requestCheckUserExist(String phone) {
        accountManager.checkUserExist(phone);
    }
}
