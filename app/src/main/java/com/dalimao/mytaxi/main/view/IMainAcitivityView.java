package com.dalimao.mytaxi.main.view;

import com.dalimao.mytaxi.account.view.IDialogView;
import com.dalimao.mytaxi.common.lbs.LocationInfo;

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

    void showLocitionChange(LocationInfo locationInfo);
}
