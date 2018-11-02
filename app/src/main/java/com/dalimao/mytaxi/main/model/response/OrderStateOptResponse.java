package com.dalimao.mytaxi.main.model.response;

import com.dalimao.mytaxi.common.biz.BaseBizResponse;

/**
 * author: apple
 * created on: 2018/10/24 下午2:12
 * description:
 */
public class OrderStateOptResponse extends BaseBizResponse{
    public final  static  int ORDER_STATE_CREATE = 0;//订单创建
    public final  static  int ORDER_STATE_CANCEL = -1;//取消订单
    public final  static  int ORDER_STATE_ACCEPT = 1;//司机接单
    public final  static  int ORDER_STATE_ARRIVE_START = 2;//司机抵达
    public final  static  int ORDER_STATE_START_DRIVE = 3;//司机开始行程
    public final  static  int ORDER_STATE_ARRIVE_END = 4;//到底目的地
    public static final int ORDER_STATE_PAY = 5;//订单支付


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
