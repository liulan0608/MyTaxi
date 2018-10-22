package com.dalimao.mytaxi.common.lbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.dalimao.mytaxi.common.util.MyLoger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: apple
 * created on: 2018/7/9 下午5:48
 * description:
 */
public class GaodeLbsYayerImpl implements  ILbsLayer{
    private static final String TAG = "GaodeLbsLayerImpl";
    private static final String KEY_MY_MARKERE = "1000";
    private Context mContext;
    //位置定位对象
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    // 地图视图对象
    private MapView mapView;
    // 地图管理对象
    private AMap aMap;
    // 地图位置变化回调对象
    private LocationSource.OnLocationChangedListener mMapLocationChangeListener;
    private boolean firstLocation = true;
    private SensorEventHelper mSensorHelper;
    private CommonLocationChangeListener mLocationChangeListener;
    private MyLocationStyle myLocationStyle;
    // 管理地图标记集合
    private Map<String, Marker> markerMap = new HashMap<>();

    private String mCity;
    public GaodeLbsYayerImpl(Context context) {
        // 创建地图对象
        mapView = new MapView(context);
        // 获取地图管理器
        aMap = mapView.getMap();
        // 创建定位对象
        mlocationClient = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);

        // 传感器对象
        mSensorHelper = new SensorEventHelper(context);
        mSensorHelper.registerSensorListener();
        mContext = context;
    }

    @Override
    public View getMapView() {
        return mapView;
    }

    @Override
    public void setLocationChangeListener(CommonLocationChangeListener locationChangeListener) {
        mLocationChangeListener = locationChangeListener;
    }

    @Override
    public void setLocationRes(int res) {
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(res));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
    }

    @Override
    public void addOrUpdateMarker(LocationInfo locationInfo, Bitmap bitmap) {
        Marker storedMarker = markerMap.get(locationInfo.getKey());
        LatLng latLng = new LatLng(locationInfo.getLatitude(),
                locationInfo.getLongitude());
        if (storedMarker != null) {
            // 如果已经存在则更新角度、位置
            storedMarker.setPosition(latLng);
            storedMarker.setRotateAngle(locationInfo.getRotation());
        } else {
            // 如果不存在则创建
            MarkerOptions options = new MarkerOptions();
            BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bitmap);
            options.icon(des);
            options.anchor(0.5f, 0.5f);
            options.position(latLng);
            Marker marker = aMap.addMarker(options);
//            marker.setRotateAngle(R.attr.rotation);
            markerMap.put(locationInfo.getKey(), marker);
            if (KEY_MY_MARKERE.equals(locationInfo.getKey())) {
                // 传感器控制我的位置标记的旋转角度
                mSensorHelper.setCurrentMarker(marker);
            }
        }
    }
    @Override
    public void onCreate(Bundle savedState) {
        mapView.onCreate(savedState);
        setUpMap();
    }

    private void setUpMap() {
        if (myLocationStyle != null) {
            aMap.setMyLocationStyle(myLocationStyle);
        }

        // 设置地图激活（加载监听）
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mMapLocationChangeListener = onLocationChangedListener;
                MyLoger.d(TAG, "activate");
                setUpLocation();
            }

            @Override
            public void deactivate() {
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
        });
        // 设置默认定位按钮是否显示，这里先不想业务使用方开放
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false，这里先不想业务使用方开放
        aMap.setMyLocationEnabled(true);
    }

    public void setUpLocation() {

        //设置监听器

        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                // 定位变化位置
                if (mMapLocationChangeListener != null) {
                    //当前城市
                    mCity = aMapLocation.getCity();
                    // 地图已经激活，通知蓝点实时更新
                    mMapLocationChangeListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                    LocationInfo locationInfo = new LocationInfo(aMapLocation.getLatitude(),
                            aMapLocation.getLongitude());
                    locationInfo.setName(aMapLocation.getPoiName());
                    locationInfo.setKey(KEY_MY_MARKERE);
                    if (firstLocation) {
                        firstLocation = false;
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(),
                                aMapLocation.getLongitude());

                        CameraUpdate up = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latLng , 17, 30, 30));
                        aMap.moveCamera(up);
                        if (mLocationChangeListener != null) {

                            mLocationChangeListener.onLocation(locationInfo);
                        }

                    }
                    if (mLocationChangeListener != null) {

                        mLocationChangeListener.onLocationChanged(locationInfo);
                    }
                }
            }
        });
        mlocationClient.startLocation();
    }
    @Override
    public String getCity() {
        return mCity;
    }

    /**
     * 重点内容，高德地图的POI 搜索接口
     * @param key
     * @param listener
     */
    @Override
    public void poiSearch(String key, final OnSearchedListener listener) {
        if (!TextUtils.isEmpty(key)){
            //1组装关键字
            InputtipsQuery inputtipsQuery = new InputtipsQuery(key,"");
            Inputtips inputtips = new Inputtips(mContext,inputtipsQuery);
            //2开始异步搜索
            inputtips.requestInputtipsAsyn();
            //3监听处理搜索结果
            inputtips.setInputtipsListener(new Inputtips.InputtipsListener() {
                @Override
                public void onGetInputtips(List<Tip> list, int code) {
                    if (code == AMapException.CODE_AMAP_SUCCESS){
                        //正确返回解析结果
                        List<LocationInfo> locationInfos = new ArrayList<LocationInfo>();
                        for (int i=0;i<list.size();i++){
                            Tip tip = list.get(i);
                            LocationInfo locationInfo = new LocationInfo(tip.getPoint().getLatitude(),tip.getPoint().getLongitude());
                            locationInfo.setName(tip.getName());
                            locationInfos.add(locationInfo);
                        }
                        listener.onSearched(locationInfos);
                    }else{
                     listener.onError(code);
                    }
                }
            });
        }

    }

    /**
     *
     * @param mStartLocation
     * @param mEndLocation
     * @param blue
     * @param onRouteCompleteListener
     */
    @Override
    public void driverRoute(LocationInfo mStartLocation, LocationInfo mEndLocation, int blue, OnRouteCompleteListener onRouteCompleteListener) {

    }

    @Override
    public void clearAllMarkers() {

    }

    @Override
    public void moveCamera(LocationInfo mStartLocation, LocationInfo mEndLocation) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        mapView.onResume();
//        setUpLocation();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        mlocationClient.stopLocation();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        mlocationClient.onDestroy();
    }


}
