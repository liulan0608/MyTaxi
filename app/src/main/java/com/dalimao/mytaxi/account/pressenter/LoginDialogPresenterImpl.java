package com.dalimao.mytaxi.account.pressenter;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.account.view.ILoginDialogView;
import com.dalimao.mytaxi.common.databus.RegisterBus;
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
    }

    @Override
    public void requestLogin(String phone, String password) {
        manager.login(phone, password);
    }

    @RegisterBus
    public void onLoginResonse(LoginResponse response){
    switch (response.getCode()){
        case IAccountManager.LOGIN_SUC:
                    view.showLoginSuc();
                    break;
                case IAccountManager.PASSWORD_ERROR:
                    view.showError(IAccountManager.PASSWORD_ERROR,"");
                    break;
                case IAccountManager.SERVER_FAIL:
                   view.showError(IAccountManager.SERVER_FAIL,"");
                    break;
    }
}

}