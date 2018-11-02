package com.dalimao.mytaxi.main.model.response;

import com.dalimao.mytaxi.common.biz.BaseBizResponse;

/**
 * author: apple
 * created on: 2018/10/24 下午2:12
 * description:
 */
public class OrderStateOptResponse extends BaseBizResponse{
    public final  static  int ORDER_STATE_CREATE = 0;
    public final  static  int ORDER_STATE_CANCEL = 1;

    private int state;

    private Order data;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Order getData() {
        return data;
    }

    public void setData(Order data) {
        this.data = data;
    }
}
