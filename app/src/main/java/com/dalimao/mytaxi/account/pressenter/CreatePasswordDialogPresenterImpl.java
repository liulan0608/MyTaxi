package com.dalimao.mytaxi.account.pressenter;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.account.view.ICreatePasswordDialogView;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.util.Global;

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
    }
    @RegisterBus
    public void onRegisterResponse(BaseBizResponse response){
        switch (response.getCode()){
            case IAccountManager.REGISTER_SIUC:
                    view.showRegisterSuc();
                    break;
                case IAccountManager.SERVER_FAIL:
                    view.showError(IAccountManager.SERVER_FAIL,"");
                    break;
        }
    }
    @RegisterBus
    public void onLoginResponse(LoginResponse response){
        switch (response.getCode()){
            case IAccountManager.LOGIN_SUC:
                view.showLoginSuc();
                break;
            case IAccountManager.SERVER_FAIL:
                view.showError(IAccountManager.SERVER_FAIL,"");
                break;
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
