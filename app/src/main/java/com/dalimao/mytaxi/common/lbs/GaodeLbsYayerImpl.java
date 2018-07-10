package com.dalimao.mytaxi.common.lbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.dalimao.mytaxi.map.SensorEventHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * author: apple
 * created on: 2018/7/9 下午5:48
 * description:
 */
public class GaodeLbsYayerImpl implements  ILbsLayer{
    private Context mContext;
    private MapView mMapView;
    private AMap aMap;//地图控制器对象，用来操作地图

    private LocationSource.OnLocationChangedListener mListener;
    private CommonLocationChangeListener mCommonLocationChangeListener;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private static final int KEY_MY_MARKERE = 1000;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    //管理地图标记集合
    private Map<Integer,Marker> markerMap = new HashMap<>();

    private LocationSource locationSource;

    public GaodeLbsYayerImpl(Context context) {
        this.mContext = context;
        //创建地图对象
        mMapView = new MapView(context);
        //获取地图管理器
        aMap = mMapView.getMap();
        //创建定位对象
        mlocationClient = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //传感器对象
        mSensorHelper = new SensorEventHelper(context);
        mSensorHelper.registerSensorListener();

        locationSource = new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                //激活定位
                mListener = onLocationChangedListener;
                mlocationClient.startLocation();
                //设置定位监听
                mlocationClient.setLocationListener(new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation amapLocation) {
                        if (mListener != null && amapLocation != null) {
                            if (amapLocation != null
                                    && amapLocation.getErrorCode() == 0) {

                                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                                LocationInfo info = new LocationInfo(amapLocation.getLatitude(), amapLocation.getLongitude());
                                info.setName(amapLocation.getPoiName());
                                info.setId(KEY_MY_MARKERE);
                                info.setRotation(amapLocation.getAccuracy());
                                if (!mFirstFix) {
                                    mFirstFix = true;
                                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                                    addMarker(location);//添加定位图标
                                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                                    if (mCommonLocationChangeListener!=null){
                                        mCommonLocationChangeListener.onLocationFirst(info);
                                    }
                                } else {
                                    mCircle.setCenter(location);
                                    mCircle.setRadius(amapLocation.getAccuracy());
                                    mLocMarker.setPosition(location);
                                    if (mCommonLocationChangeListener!=null){
                                        mCommonLocationChangeListener.onLocationChanged(info);
                                    }
                                }
                                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                            } else {
                                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                                MyLoger.d("AmapErr",errText);

                            }
                        }
                    }
                });
            }
            @Override
            public void deactivate() {
                //停止定位
                mListener = null;
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
        };
    }

    @Override
    public View getMapView() {
        return mMapView;
    }

    @Override
    public void setLocationChangeListener(CommonLocationChangeListener listener) {
    this.mCommonLocationChangeListener = listener;
    }

    @Override
    public void onCreate(Bundle state) {
        mMapView.onCreate(state);
        setUpMap();
    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(locationSource);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }
    @Override
    public void onResume() {
        mMapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mMapView.onPause();
        locationSource.deactivate();
        mFirstFix = false;
    }

    @Override
    public void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(mContext.getResources(),
                R.mipmap.location_marker);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//    BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle("mylocation");
    }

}
