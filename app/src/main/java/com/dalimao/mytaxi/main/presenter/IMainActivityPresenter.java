package com.dalimao.mytaxi.main.presenter;

/**
 * author: apple
 * created on: 2018/6/27 上午10:20
 * description:
 */
public interface IMainActivityPresenter {
    void requestLoginByToken();

    void fetchNearDrivers(double latitude, double longitude);
}
