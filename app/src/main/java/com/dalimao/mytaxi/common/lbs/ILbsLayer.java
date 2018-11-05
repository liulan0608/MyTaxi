package com.dalimao.mytaxi.common.lbs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.dalimao.mytaxi.main.view.MainActivity;

import java.util.List;

/**
 * author: apple
 * created on: 2018/7/9 下午5:47
 * description:
 */
public interface ILbsLayer {
    /**
     * 获取地图
     */
    View getMapView();
    /**
     * 设置位置变化监听
     */
    void setLocationChangeListener(CommonLocationChangeListener listener);

    /**
     * 设置蓝点定位图标
     */
    void setLocationRes(int res);
    /**
     * 添加，更新标记点，包括位置、角度（通过 id 识别）
     */
    void addOrUpdateMarker(LocationInfo info, Bitmap bitmap);

    /**
     * 生存周期
     * @param state
     */
    void onCreate(Bundle state);
    void onResume();
    void onSaveInstanceState(Bundle outState);
    void onPause();
    void onDestroy();

    /**
     * 获取当前城市
     * @return
     */
    String getCity();

    /**
     * 联动搜索附近的位置
     * @param s
     * @param listener
     */
    void poiSearch(String s, OnSearchedListener listener);

    /**
     * 绘制亮点之间的行车路径
     * @param mStartLocation
     * @param mEndLocation
     * @param blue
     * @param onRouteCompleteListener
     */
    void driverRoute(LocationInfo mStartLocation, LocationInfo mEndLocation, int blue, OnRouteCompleteListener onRouteCompleteListener);

    void clearAllMarkers();

    void moveCamera(LocationInfo mStartLocation, LocationInfo mEndLocation);
    //移动相机到某个点
    void moveCameraToPoint(LocationInfo locationInfo, int scale);

    /**
     * created on: 2018/7/9 下午5:53
     * description:
     */
    interface CommonLocationChangeListener {
        void onLocationChanged(LocationInfo info);
        void onLocation(LocationInfo info);
    }
    // TODO: 2018/7/9 ipo搜索
     interface OnSearchedListener {
        void onSearched(List<LocationInfo> results);
        void onError(int code);
    }

     interface OnRouteCompleteListener {
         void onComplete(RouteInfo result);
    }
}
