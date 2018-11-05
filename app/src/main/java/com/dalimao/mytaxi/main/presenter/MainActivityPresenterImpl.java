package com.dalimao.mytaxi.main.presenter;

import android.content.Context;

import com.dalimao.mytaxi.account.model.AccountManagerImpl;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.main.model.IMainManager;
import com.dalimao.mytaxi.main.model.MainManagerImpl;
import com.dalimao.mytaxi.main.model.response.NearByDriverResponse;
import com.dalimao.mytaxi.main.model.response.Order;
import com.dalimao.mytaxi.main.model.response.OrderStateOptResponse;
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
    Context mContext;
    public Order mCurrentOrder;
    public MainActivityPresenterImpl(Context context,IMainAcitivityView view) {
        this.mContext = context;
        this.view = view;
        manager = new AccountManagerImpl();
        mainManager = new MainManagerImpl(context);
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

    @Override
    public void callDriver(String mPushKey, float mCost, LocationInfo mStartLocation, LocationInfo mEndLocation) {
        mainManager.callDriver(mPushKey,mCost,mStartLocation,mEndLocation);
    }

    @Override
    public void getOrderList() {
        mainManager.getOrderList();
    }

    @Override
    public void cancelOrder() {
        if (mCurrentOrder!=null){
            mainManager.cancelOrder(mCurrentOrder.getOrderId());
        }else{
            view.showLoginSuc();
        }

    }

    @Override
    public void orderPay() {
        mainManager.pay(mCurrentOrder.getOrderId());
    }

    @Override
    public void getProcessingOrder() {
        mainManager.getProcessOrder();
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
 *司机位置改变
 */@RegisterBus
    public void onLocationInfo(LocationInfo locationInfo){
    if (mCurrentOrder!=null&&mCurrentOrder.getState() == OrderStateOptResponse.ORDER_STATE_ACCEPT){
        view.updateDriver2StartRoute(locationInfo,mCurrentOrder);
    }else if (mCurrentOrder!=null&&mCurrentOrder.getState() == OrderStateOptResponse.ORDER_STATE_START_DRIVE){
        view.updateDriver2StartRoute(locationInfo,mCurrentOrder);
    }else {
        view.showLocationChange(locationInfo);
    }
    }
   /**
 *司机接单
 */@RegisterBus
    public void onDriverOptOrder(Order order){
       mCurrentOrder = order;
       if (order.getState() == OrderStateOptResponse.ORDER_STATE_ACCEPT){
           //司机接单
           view.showDriverAcceptOrder(order);
       }else if(order.getState() == OrderStateOptResponse.ORDER_STATE_ARRIVE_START){
           //司机抵达上车点
            view.showDriverArriveStart(order);
       }else if(order.getState() == OrderStateOptResponse.ORDER_STATE_START_DRIVE){
           //乘客上车，开始行程
           view.showStartDrive(order);
       }else if(order.getState() == OrderStateOptResponse.ORDER_STATE_ARRIVE_END){
           //乘客到达，结束行程
           view.showArriveEnd(order);
       }

    }
    /**
     * 呼叫司机响应--订单状态的响应
     * @param response
     */
    @RegisterBus
    public void responseCallDriver(OrderStateOptResponse response){
    if (response.getState() == OrderStateOptResponse.ORDER_STATE_CREATE){
        if (response.getCode() == BaseBizResponse.STATE_OK){

            //保存当前的订单
            mCurrentOrder = response.getData();
            view.showCallDriverSuc(mCurrentOrder);
        }else{
            view.showCallDriverFail();
        }

    }else if(response.getState() == OrderStateOptResponse.ORDER_STATE_CANCEL){
        if (response.getCode() == BaseBizResponse.STATE_OK){
            view.showCancelSuc();
        }else{
            view.showCancelFail();
        }
    }else if(response.getState() == OrderStateOptResponse.ORDER_STATE_PAY){
        if (response.getCode() == BaseBizResponse.STATE_OK){
            view.showPaySuc(mCurrentOrder);
        }else{
            view.showPayFail();
        }
    }

}


}
