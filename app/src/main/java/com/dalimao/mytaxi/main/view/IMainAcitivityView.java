package com.dalimao.mytaxi.main.view;

import com.dalimao.mytaxi.account.view.IDialogView;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.main.model.response.Order;

import java.util.List;

/**
 * author: apple
 * created on: 2018/6/27 上午10:26
 * description:
 */
public interface IMainAcitivityView extends IDialogView {
    /**
     * 显示登陆成功
     */
    void showLoginSuc();

    /**
     * 显示附近的司机
     * @param data
     */
    void showNears(List<LocationInfo> data);

    /**
     * 位置发生改变
     * @param locationInfo
     */

    void showLocitionChange(LocationInfo locationInfo);

    /**
     * 呼叫司机成功
     */
    void showCallDriverSuc();

    /***
     * 呼叫司机失败
     */
    void showCallDriverFail();

    /**
     * 取消订单成功
     */
    void showCancelSuc();

    /**
     * 取消订单失败
     */
    void showCancelFail();

    /**
     * 司机接单
     * @param order
     */

    void driverReceiveOrder(Order order);
}
