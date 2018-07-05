package com.dalimao.mytaxi.account.pressenter;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.util.Global;
import com.dalimao.mytaxi.main.IMainAcitivityView;

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
    }


    @Override
    public void requestLoginByToken() {
        manager.loginByToken();
    }

    @RegisterBus
    public void responseLonginByToken(LoginResponse response){
        switch (response.getCode()){
            case  IAccountManager.TOKEN_INVALID:
                view.showError(IAccountManager.TOKEN_INVALID,"");
                break;
            case IAccountManager.LOGIN_SUC:
                view.showLoginSuc();
                break;
    }

}
}
