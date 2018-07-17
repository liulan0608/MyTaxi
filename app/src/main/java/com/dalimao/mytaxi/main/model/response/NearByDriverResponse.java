package com.dalimao.mytaxi.main.model.response;

import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.lbs.LocationInfo;

import java.util.List;

/**
 * author: apple
 * created on: 2018/7/13 下午3:47
 * description:
 */
public class NearByDriverResponse extends BaseBizResponse{

    List<LocationInfo> data;

    public List<LocationInfo> getData() {
        return data;
    }

    public void setData(List<LocationInfo> data) {
        this.data = data;
    }
}
