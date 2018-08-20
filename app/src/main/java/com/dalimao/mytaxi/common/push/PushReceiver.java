package com.dalimao.mytaxi.common.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dalimao.mytaxi.common.util.MyLoger;

import cn.bmob.push.PushConstants;

/**
 * author: apple
 * created on: 2018/7/31 下午3:52
 * description:
 */
//TODO 集成：1.3、创建自定义的推送消息接收器，并在清单文件中注册
public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            MyLoger.d("bmob", "客户端收到推送内容："+intent.getStringExtra("msg"));
            MyLoger.toast(context,"收到广播："+intent.getStringExtra("msg"));
        }
    }

}