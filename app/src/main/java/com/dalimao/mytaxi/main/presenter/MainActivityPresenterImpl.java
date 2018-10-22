package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.main.model.IMainManager;
import com.dalimao.mytaxi.main.model.MainManagerImpl;
import com.dalimao.mytaxi.main.model.response.NearByDriverResponse;
import com.dalimao.mytaxi.main.view.IMainAcitivityView;

/**
 * author: apple
 * created on: 2018/6/27 上午10:22
 * description:
 */
public class MainActivityPresenterImpl implements IMainActivityPresenter {
    IAccountManager manager;
    IMainAcitivityView view;
    IMainManager mainManager;
    public MainActivityPresenterImpl(IMainAcitivityView view) {
        this.view = view;
        manager = new AccountManagerImpl();
        mainManager = new MainManagerImpl();
    }

    /**
     * token 登陆
     */
    @Override
    public void requestLoginByToken() {
        manager.loginByToken();
    }

    /**
     * 获取附近的司机
     * @param latitude
     * @param longitude
     */
    @Override
    public void fetchNearDrivers(double latitude, double longitude) {
        mainManager.fetchNearDrivers(latitude,longitude);
    }

    @Override
    public void updateLocationToServer(LocationInfo locationInfo) {
        mainManager.updateLocationToServer(locationInfo);
    }

    /**
     * 登陆返回结果
     * @param response
     */
    @RegisterBus
    public void responseLonginByToken(LoginResponse response) {
        switch (response.getCode()) {
            case IAccountManager.TOKEN_INVALID:
                view.showError(IAccountManager.TOKEN_INVALID, "");
                break;
            case IAccountManager.LOGIN_SUC:
                view.showLoginSuc();
                break;
        }
    }/**
     * 获取附近司机返回结果
     * @param response
     */
    @RegisterBus
    public void responseNearDrivers(NearByDriverResponse response){
        switch (response.getCode()){
            case  BaseBizResponse.STATE_OK:
                view.showNears(response.getData());
                break;

    }

}

/**
 *
 */@RegisterBus
    public void onLocationInfo(LocationInfo locationInfo){
    view.showLocitionChange(locationInfo);
}
}
