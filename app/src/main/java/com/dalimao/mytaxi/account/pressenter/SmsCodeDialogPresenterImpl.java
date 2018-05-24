package com.dalimao.mytaxi.account.pressenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.view.ISmsCodeDialogView;

import java.lang.ref.WeakReference;

/**
 * author: apple
 * created on: 2018/5/24 下午2:48
 * description:
 */
public class SmsCodeDialogPresenterImpl implements ISmsCodeDialogPresenter{
    private ISmsCodeDialogView view;
    private IAccountManager accountManager;

    /**
     * 接收消息并处理
     */
    private  static  class MyHandler extends Handler{
        WeakReference<SmsCodeDialogPresenterImpl> refContext;

        public MyHandler(SmsCodeDialogPresenterImpl context) {
          refContext = new WeakReference<SmsCodeDialogPresenterImpl>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            SmsCodeDialogPresenterImpl presenter = refContext.get();
            switch (msg.what){
                case IAccountManager.SMS_SEND_SUC:
                    //下发验证码成功
                    presenter.view.showCountDownTimer();
                   break;
                case IAccountManager.SMS_SEND_FAIL:
                    //验证码发送失败
                    presenter.view.showError(IAccountManager.SMS_SEND_FAIL,"");
                    break;
                case IAccountManager.SMS_CHECK_SUC:
                    //检验验证码正确
                    presenter.view.showSmsCodeCheckState(true);
                    break;
                case IAccountManager.SMS_CHECK_FAIL:
                    presenter.view.showError(IAccountManager.SMS_CHECK_FAIL,"");
                    break;
                case IAccountManager.USER_EXIST:
                    presenter.view.showUserExist(true);
                    break;
                case IAccountManager.USER_NOT_EXIST:
                    presenter.view.showUserExist(false);
                    break;

            }
        }
    }
    public SmsCodeDialogPresenterImpl(ISmsCodeDialogView view, IAccountManager accountManager) {
        this.view = view;
        this.accountManager = accountManager;
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
