package com.dalimao.mytaxi.main.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.main.presenter.IMainActivityPresenter;
import com.dalimao.mytaxi.main.presenter.MainActivityPresenterImpl;
import com.dalimao.mytaxi.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.lbs.GaodeLbsYayerImpl;
import com.dalimao.mytaxi.common.lbs.ILbsLayer;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.util.MyLoger;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       presenter = new MainActivityPresenterImpl(this);
//        presenter.requestLoginByToken();
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
            public void onLocationFirst(LocationInfo info) {
                //首次定位，添加当前位置的标记
                getNearDrivers(info.getLatitude(),info.getLongitude());
            }
        });
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.activity_main);
        mapViewContainer.addView(mLbsLayer.getMapView());
    }

    /**
     * 获取附近的司机
     * @param latitude
     * @param longitude
     */
    private void getNearDrivers(double latitude, double longitude) {
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
