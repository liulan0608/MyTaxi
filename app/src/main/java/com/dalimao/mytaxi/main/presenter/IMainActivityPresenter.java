package com.dalimao.mytaxi.main.presenter;

import com.dalimao.mytaxi.common.lbs.LocationInfo;

/**
 * author: apple
 * created on: 2018/6/27 上午10:20
 * description:
 */
public interface IMainActivityPresenter {
    void requestLoginByToken();
/**
 * 获取附近司机
 */
    void fetchNearDrivers(double latitude, double longitude);

    /**
     * 上报当前位职
     * @param locationInfo
     */
    void updateLocationToServer(LocationInfo locationInfo);
}
