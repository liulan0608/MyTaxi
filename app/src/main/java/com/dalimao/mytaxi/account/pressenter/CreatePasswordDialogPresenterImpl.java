package com.dalimao.mytaxi.account.pressenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.view.ICreatePasswordDialogView;
import com.dalimao.mytaxi.common.util.Global;

import java.lang.ref.WeakReference;

/**
 * author: apple
 * created on: 2018/6/26 下午5:34
 * description:
 */
public class CreatePasswordDialogPresenterImpl implements ICreatePassordDialogPresenter{
    ICreatePasswordDialogView view;
    IAccountManager manager;

    public CreatePasswordDialogPresenterImpl(ICreatePasswordDialogView view) {
        this.view = view;
        this.manager = new AccountManagerImpl(Global.sharedPrefDao);
        manager.setHandler(new MyHandler(this));
    }
    public class MyHandler extends Handler{
        WeakReference<CreatePasswordDialogPresenterImpl> createPswRef;
        public MyHandler(CreatePasswordDialogPresenterImpl createPasswordDialogPresenter) {
            createPswRef =new WeakReference<CreatePasswordDialogPresenterImpl>(createPasswordDialogPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            CreatePasswordDialogPresenterImpl presenter =createPswRef.get();
            switch (msg.what){
                case IAccountManager.REGISTER_SIUC:
                    presenter.view.showRegisterSuc();
                    break;
                case IAccountManager.LOGIN_SUC:
                    presenter.view.showLoginSuc();
                    break;
                case IAccountManager.PASSWORD_ERROR:
                    presenter.view.showError(IAccountManager.PASSWORD_ERROR,"");
                    break;
                case IAccountManager.SERVER_FAIL:
                    presenter.view.showError(IAccountManager.SERVER_FAIL,"");
                    break;
            }
        }
    }
    @Override
    public void checkPw(String pw, String pw1) {

    }

    @Override
    public void requestRegister(String phone, String pw) {
        manager.register(phone,pw);
    }

    @Override
    public void requestLogin(String phone, String pw) {
        manager.login(phone,pw);
    }
}
