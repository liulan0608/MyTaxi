package com.dalimao.mytaxi.main.model;

import com.dalimao.mytaxi.common.lbs.LocationInfo;

/**
 * author: apple
 * created on: 2018/7/10 下午5:30
 * description:
 */
public interface IMainManager {
    //服务器错误
    int SERVER_FAIL = -999;

    /**
     * 获取附近的司机
     * @param latitude
     * @param longitude
     */
    void fetchNearDrivers(double latitude, double longitude);

    /**
     * 上报当前位置
     * @param locationInfo
     */
    void updateLocationToServer(LocationInfo locationInfo);

    /**
     * 呼叫司机
     * @param mPushKey
     * @param mCost
     * @param mStartLocation
     * @param mEndLocation
     */
    void callDriver(String mPushKey, float mCost, LocationInfo mStartLocation, LocationInfo mEndLocation);

    /**
     * 获取订单列表
     */
    void getOrderList();

    /**
     * 取消订单
     */
    void cancelOrder(String id);
}
