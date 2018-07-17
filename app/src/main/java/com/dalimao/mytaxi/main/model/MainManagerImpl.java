package com.dalimao.mytaxi.main.model;

import android.content.Context;

import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.util.SaveData_withPreferences;
import com.dalimao.mytaxi.main.model.response.NearByDriverResponse;
import com.google.gson.Gson;

import rx.functions.Func1;


/**
 * author: apple
 * created on: 2018/7/10 下午5:31
 * description:
 */
public class MainManagerImpl implements IMainManager {
    IHttpClient httpClient;
    //数据存储
    private SaveData_withPreferences preferences;
    //上下文
    private Context mContext;

    public MainManagerImpl() {
        httpClient = new OkHttpClientImpl();
        preferences = new SaveData_withPreferences(mContext);
    }

    /**
     * 获取附近的司机
     * @param latitude
     * @param longitude
     */
    @Override
    public void fetchNearDrivers(final double latitude, final double longitude) {
        RxBus.getInstance().chainProcess(new Func1() {
            @Override
            public Object call(Object o) {
                IRequest request = new BaseRequest(API.GET_NEARBY_DRIVER);
                request.setBody("latitude",new Double(latitude).toString());
                request.setBody("longitude",new Double(longitude).toString());
                IResponse response = httpClient.get(request,false);
                NearByDriverResponse nearByDriverResponse = new NearByDriverResponse();
                if (response.getCode()== BaseResponse.STATE_OK){
                    nearByDriverResponse= new Gson().fromJson(response.getData(),NearByDriverResponse.class);
                    return nearByDriverResponse;
                }else {
                   return  null;
                }
            }
        });
    }
}
