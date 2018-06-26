package com.dalimao.mytaxi.account.pressenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.view.ILoginDialogView;
import com.dalimao.mytaxi.common.util.Global;

/**
 * author: apple
 * created on: 2018/6/26 下午6:10
 * description:
 */
public class LoginDialogPresenterImpl implements ILoginDialogPresenter {
    ILoginDialogView view;
    IAccountManager manager;

    public LoginDialogPresenterImpl(ILoginDialogView view) {
        this.view = view;
        manager = new AccountManagerImpl(Global.sharedPrefDao);
        manager.setHandler(new MyHandler(this));
    }

    @Override
    public void requestLogin(String phone, String password) {
        manager.login(phone,password);
    }

    private class MyHandler extends Handler {
        public MyHandler(LoginDialogPresenterImpl loginDialogPresenter) {

        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case
            }
        }
    }
}
