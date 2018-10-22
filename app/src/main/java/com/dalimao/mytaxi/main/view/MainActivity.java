package com.dalimao.mytaxi.main.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.lbs.GaodeLbsYayerImpl;
import com.dalimao.mytaxi.common.lbs.ILbsLayer;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.dalimao.mytaxi.main.presenter.IMainActivityPresenter;
import com.dalimao.mytaxi.main.presenter.MainActivityPresenterImpl;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * author: apple
 * created on: 2018/5/15 上午9:52
 * description:
 * 1 检查本地记录（登录状态检查）
 * 2 若用户没有登陆则登陆
 * 3 登陆之前先校验手机号
 * 4 token 有效使用 token 自动登录
 * -----地图初始化-----
 * 1.地图接入
 * 2.定位自己的位置，显示蓝点
 * 3.使用marker标记当前位置和方向
 * 4.地图的封装
 *
 * ----司机-----
 * 1、获取附近的司机
 */
public class MainActivity extends AppCompatActivity implements IMainAcitivityView {
    private IMainActivityPresenter presenter;
    private ILbsLayer mLbsLayer;

    private Bitmap mDriverBit;

    private String mPushKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainActivityPresenterImpl(this);
        presenter.requestLoginByToken();
        RxBus.getInstance().register(presenter);
        //地图服务
        mLbsLayer = new GaodeLbsYayerImpl(this);
        mLbsLayer.onCreate(savedInstanceState);
        mLbsLayer.setLocationChangeListener(new ILbsLayer.CommonLocationChangeListener() {
            @Override
            public void onLocationChanged(LocationInfo info) {
                //位置改变

            }

            @Override
            public void onLocation(LocationInfo locationInfo) {
                // 首次定位，添加当前位置的标记
                mLbsLayer.addOrUpdateMarker(locationInfo,
                        BitmapFactory.decodeResource(getResources(),
                                R.mipmap.location_marker));
                //获取附近司机
                getNearDrivers(locationInfo.getLatitude(),locationInfo.getLongitude());

                //上报当前位置
                updateLocationToServer(locationInfo);
            }
        });
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_main);
        mapViewContainer.addView(mLbsLayer.getMapView());

        //TODO 集成：1.4、初始化数据服务SDK、初始化设备信息并启动推送服务
        // 初始化BmobSDK
        Bmob.initialize(this, API.Config.getAppId());
        // 使用推送服务时的初始化操作
        BmobInstallation bmobInstallation = BmobInstallation.getCurrentInstallation(this);
        bmobInstallation.save();
        mPushKey = bmobInstallation.getInstallationId();
        // 启动推送服务
        BmobPush.startWork(this);
    }

    /**
     * 上报当前位置
     * @param locationInfo
     */
    private void updateLocationToServer(LocationInfo locationInfo) {
        locationInfo.setKey(mPushKey);
        presenter.updateLocationToServer(locationInfo);
    }

    /**
     * 获取附近的司机
     * @param latitude
     * @param longitude
     */
    private void getNearDrivers(double latitude, double longitude) {
        MyLoger.d("latitude:"+latitude+",longitude:"+longitude);
        presenter.fetchNearDrivers(latitude,longitude);
    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        PhoneInputDialog dialog= new PhoneInputDialog(this);
        dialog.show();
    }

    @Override
    public void showLoginSuc() {
        MyLoger.toast(this,"登陆成功");
    }

    /**
     * 显示附近的司机
     * @param data
     */
    @Override
    public void showNears(List<LocationInfo> data) {

        for (LocationInfo info : data){
            showLocitionChange(info);
        }
    }

    @Override
    public void showLocitionChange(LocationInfo locationInfo) {
        if (mDriverBit == null || mDriverBit.isRecycled()){
            mDriverBit = BitmapFactory.decodeResource(getResources(),R.mipmap.car);
        }
        mLbsLayer.addOrUpdateMarker(locationInfo,mDriverBit);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError(int Code, String msg) {
        switch (Code){
            case IAccountManager.TOKEN_INVALID:
                showPhoneInputDialog();
                break;
            case IAccountManager.SERVER_FAIL:
                MyLoger.toast(this,"网络繁忙");
                showPhoneInputDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unRegister(presenter);
        mLbsLayer.onDestroy();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mLbsLayer.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mLbsLayer.onPause();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       mLbsLayer.onSaveInstanceState(outState);
    }
}
