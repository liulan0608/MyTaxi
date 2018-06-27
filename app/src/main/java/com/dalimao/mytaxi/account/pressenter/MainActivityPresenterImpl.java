package com.dalimao.mytaxi.account.pressenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.common.util.Global;
import com.dalimao.mytaxi.main.IMainAcitivityView;

import java.lang.ref.WeakReference;

/**
 * author: apple
 * created on: 2018/6/27 上午10:22
 * description:
 */
public class MainActivityPresenterImpl implements IMainActivityPresenter {
    IAccountManager manager;
    IMainAcitivityView view;
    public MainActivityPresenterImpl(IMainAcitivityView view) {
        this.view = view;
        manager = new AccountManagerImpl(Global.sharedPrefDao);
        manager.setHandler(new MyHandler(this));
    }


    @Override
    public void requestLoginByToken() {
        manager.loginByToken();
    }

    private class MyHandler extends Handler {
        WeakReference<MainActivityPresenterImpl> mainRef;
        public MyHandler(MainActivityPresenterImpl mainActivityPresenter) {
            mainRef = new WeakReference<MainActivityPresenterImpl>(mainActivityPresenter);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivityPresenterImpl presenter = mainRef.get();
            switch (msg.what){
                case  IAccountManager.TOKEN_INVALID:
                    presenter.view.showError(IAccountManager.TOKEN_INVALID,"");
                    break;
                case IAccountManager.LOGIN_SUC:
                    presenter.view.showLoginSuc();
                    break;
            }
        }
    }
}
