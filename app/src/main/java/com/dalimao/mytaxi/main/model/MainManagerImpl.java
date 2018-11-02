package com.dalimao.mytaxi.main.model;

import android.content.Context;

import com.dalimao.mytaxi.account.model.response.Login;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.dalimao.mytaxi.common.util.SaveData_withPreferences;
import com.dalimao.mytaxi.main.model.response.NearByDriverResponse;
import com.dalimao.mytaxi.main.model.response.Order;
import com.dalimao.mytaxi.main.model.response.OrderStateOptResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import rx.functions.Func1;


/**
 * author: apple
 * created on: 2018/7/10 下午5:31
 * description:
 */
public class MainManagerImpl implements IMainManager {
    IHttpClient httpClient;
    //数据存储
    private SaveData_withPreferences preferences;
    //上下文
    private Context mContext;

    public MainManagerImpl(Context context) {
        this.mContext = context;
        httpClient = new OkHttpClientImpl();
        preferences = new SaveData_withPreferences(context);
    }

    /**
     * 获取附近的司机
     * @param latitude
     * @param longitude
     */
    @Override
    public void fetchNearDrivers(final double latitude, final double longitude) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.GET_NEARBY_DRIVER);
                request.setBody("latitude",new Double(latitude).toString());
                request.setBody("longitude",new Double(longitude).toString());
                IResponse response = httpClient.get(request,false);
                if (response.getCode()== BaseResponse.STATE_OK){
                    try {
                        NearByDriverResponse  nearByDriverResponse= new Gson().fromJson(response.getData(),NearByDriverResponse.class);
                        return nearByDriverResponse;
                    } catch (JsonSyntaxException e) {
                        return  null;
                    }
                }else {
                   return  null;
                }
            }
        });
    }

    /**
     * 上报当前位置
     * @param locationInfo
     */
    @Override
    public void updateLocationToServer(final LocationInfo locationInfo) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.UPDATE_LOCATION);
                request.setBody("latitude",
                        new Double(locationInfo.getLatitude()).toString());
                request.setBody("longitude",
                        new Double(locationInfo.getLongitude()).toString());
                request.setBody("rotation",
                        new Float(locationInfo.getRotation()).toString());
                request.setBody("key", locationInfo.getKey());
                IResponse response = httpClient.post(request, false);
                if (response.getCode() == BaseResponse.STATE_OK) {
                    MyLoger.d("mainManagerImpl", "位置上报成功");
                } else {
                    MyLoger.d("mainManagerImpl", "位置上报失败");
                }
            return null;
            }
        });
    }

    /**
     * 呼叫司机
     * @param mPushKey
     * @param mCost
     * @param mStartLocation
     * @param mEndLocation
     */
    @Override
    public void callDriver(final String mPushKey, final float mCost,
                           final LocationInfo mStartLocation, final LocationInfo mEndLocation) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Login login = (Login) preferences.getData_object(SaveData_withPreferences.KEY_ACCOUNT, Login.class);
                String uid = login.getUid();
                String phone = login.getAccount();
                IRequest request = new BaseRequest(API.CALL_DRIVER);
                request.setBody("key",mPushKey);
                request.setBody("uid",uid);
                request.setBody("startLatitude",String.valueOf(mStartLocation.getLatitude()));
                request.setBody("startLongitude",String.valueOf(mStartLocation.getLongitude()));
                request.setBody("endLatitude",String.valueOf(mEndLocation.getLatitude()));
                request.setBody("endLongitude",String.valueOf(mEndLocation.getLongitude()));
                request.setBody("startAddr",mStartLocation.getName());
                request.setBody("endAddr",mEndLocation.getName());
                request.setBody("phone",phone);
                request.setBody("cost",String.valueOf(mCost));
                IResponse response = httpClient.post(request,false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                if (response.getCode() == BaseResponse.STATE_OK) {
                    orderStateOptResponse= new Gson().fromJson(response.getData(),OrderStateOptResponse.class);
                }
                    orderStateOptResponse.setCode(response.getCode());
                    orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CREATE);
                MyLoger.d("call driver:"+ response.getCode());
                MyLoger.d("call driver phone: "+ phone);

                return orderStateOptResponse;
            }
        });
    }

    /**
     * 获取订单列表
     */
    @Override
    public void getOrderList() {
        Login login = (Login) preferences.getData_object(SaveData_withPreferences.KEY_ACCOUNT, Login.class);
        String uid = login.getUid();
        IRequest request = new BaseRequest(API.GET_ORDER_LIST);
        request.setBody("uid",uid);
        IResponse response = httpClient.post(request,false);
        if (response.getCode()== BaseBizResponse.STATE_OK){

        }

    }

    /**
     * 取消订单
     */
    @Override
    public void cancelOrder(final String id) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.CANCEl_ORDER);
                request.setBody("id",id);
                IResponse response = httpClient.post(request,false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_CANCEL);
                return orderStateOptResponse;
            }
        });


    }

    /**
     *
     */
    @Override
    public void pay(final String orderId) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.PAY_SUCCESS);
                request.setBody("id",orderId);
                IResponse response = httpClient.post(request,false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                orderStateOptResponse.setCode(response.getCode());
                orderStateOptResponse.setState(OrderStateOptResponse.ORDER_STATE_PAY);
                return orderStateOptResponse;
            }
        });
    }

    @Override
    public void getProcessOrder() {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                Login login = (Login) preferences.getData_object(SaveData_withPreferences.KEY_ACCOUNT, Login.class);
                String uid = login.getUid();
                IRequest request = new BaseRequest(API.GET_PROCESSING_ORDER);
                request.setBody("id",uid);
                IResponse response = httpClient.post(request,false);
                OrderStateOptResponse orderStateOptResponse = new OrderStateOptResponse();
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    orderStateOptResponse = new Gson().fromJson(response.getData(),OrderStateOptResponse.class);
                    orderStateOptResponse.setState(orderStateOptResponse.getData().getState());
                    orderStateOptResponse.setCode(response.getCode());
                    return orderStateOptResponse;
                }
                return null;
            }
        });
    }
}
