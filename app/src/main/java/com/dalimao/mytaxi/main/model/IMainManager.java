package com.dalimao.mytaxi.main.model;

/**
 * Created by liuguangli on 17/5/31.
 */

public interface IMainManager {

    /**
     *  获取附近司机
     * @param latitude
     * @param longitude
     */
    void fetchNearDrivers(double latitude, double longitude);

}