package com.dalimao.mytaxi.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.dalimao.mytaxi.main.model.response.Order;
import com.dalimao.mytaxi.main.model.response.OrderStateOptResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * author: apple
 * created on: 2018/7/31 下午3:52
 * description:
 */
//TODO 集成：1.3、创建自定义的推送消息接收器，并在清单文件中注册
public class MyPushMessageReceiver extends BroadcastReceiver {

    private static final int MSG_TYPE_LOCATION = 1;
    private static final int MSG_TYPE_DRIVE_ORDER = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String msg = intent.getStringExtra("msg");

            MyLoger.d("客户端收到推送内容："+msg);

            try {
                JSONObject jsonObject = new JSONObject(msg);
                int type = jsonObject.optInt("type");
                if (type == MSG_TYPE_LOCATION){
                    //位置变化
                    LocationInfo locationInfo = new Gson().fromJson(jsonObject.optString("data"),LocationInfo.class);
                    RxBus.getInstance().send(locationInfo);
                }else if(type == MSG_TYPE_DRIVE_ORDER){
                    Order order = new Gson().fromJson(jsonObject.optString("data"),Order.class);
                    RxBus.getInstance().send(order);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}