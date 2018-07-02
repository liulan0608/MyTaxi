package com.dalimao.mytaxi.common.databus;

/**
 * author: apple
 * created on: 2018/6/29 下午3:12
 * description:
 */
public interface IDataBusSubscriber {
    void onEvent(Object data);
}
