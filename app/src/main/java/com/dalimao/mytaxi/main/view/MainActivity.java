package com.dalimao.mytaxi.main.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.lbs.GaodeLbsYayerImpl;
import com.dalimao.mytaxi.common.lbs.ILbsLayer;
import com.dalimao.mytaxi.common.lbs.LocationInfo;
import com.dalimao.mytaxi.common.lbs.RouteInfo;
import com.dalimao.mytaxi.common.util.DevUtil;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.dalimao.mytaxi.main.model.response.Order;
import com.dalimao.mytaxi.main.presenter.IMainActivityPresenter;
import com.dalimao.mytaxi.main.presenter.MainActivityPresenterImpl;

import java.util.ArrayList;
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
    //起点与终点
    private AutoCompleteTextView mStartEdit;
    private AutoCompleteTextView mEndEdit;
    private PoiAdapter mEndAdapter;
    //标题栏显示当前城市
    private TextView mCity;
    //记录起点和终点
    private LocationInfo mStartLocation;
    private LocationInfo mEndLocation;
    private  Bitmap mStartBit;
    private  Bitmap mEndBit;
    private  Bitmap mLocationBit;

    //当前是否登陆
    private boolean mIsLogin;
     //当前是否定位
    private boolean mIsLocate;
//    操作状态相关元素
    private View mOptArea;
    private View mLoadingArea;
    private TextView mTips;
    private TextView mLoadingText;
    private Button mBtnCancel;
    private Button mBtnCall;
    private Button mBtnPay;
    private float mCost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainActivityPresenterImpl(this,this);
        RxBus.getInstance().register(presenter);
        presenter.requestLoginByToken();
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
                //记录起点
                mStartLocation = locationInfo;
                //设置标题
                mCity.setText(mLbsLayer.getCity());
                //设置起点
                mStartEdit.setText(locationInfo.getName());
                //获取附近司机
                getNearDrivers(locationInfo.getLatitude(),locationInfo.getLongitude());
                //上报当前位置
                updateLocationToServer(locationInfo);
                //获取待处理订单
                mIsLocate = true;
                getProcessingOrder();
            }
        });
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_container);
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
        //初始化其他视图元素
        initView();
    }

    private void initView() {
        mStartEdit = (AutoCompleteTextView) findViewById(R.id.start);
        mEndEdit = (AutoCompleteTextView) findViewById(R.id.end);
        mCity = (TextView) findViewById(R.id.city);
        mOptArea = findViewById(R.id.optArea);
        mLoadingArea = findViewById(R.id.loading_area);
        mTips  = (TextView) findViewById(R.id.tips_info);
        mLoadingText  = (TextView) findViewById(R.id.loading_text);
        mBtnCall = (Button) findViewById(R.id.btn_call_driver);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnPay = (Button) findViewById(R.id.btn_pay);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_call_driver:
                        //呼叫司机
                        callDriver();
                        break;
                    case R.id.btn_cancel:
//                        取消
                        cancelOrder();
                        break;
                    case R.id.btn_pay:
//                        支付
                        orderPay();
                        break;
                }
            }
        };
        mBtnCall.setOnClickListener(listener);
        mBtnCancel.setOnClickListener(listener);
        mBtnPay.setOnClickListener(listener);
        mEndEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //关键字搜索推荐地点
                mLbsLayer.poiSearch(s.toString(), new ILbsLayer.OnSearchedListener() {
                    @Override
                    public void onSearched(List<LocationInfo> results) {
                        updatePoiList(results);
                    }

                    @Override
                    public void onError(int code) {

                    }
                });
            }
        });

    }

    /**
     * 支付
     */
    private void orderPay() {
        mLoadingArea.setVisibility(View.VISIBLE);
        mTips.setVisibility(View.GONE);
        mLoadingText.setText(getResources().getString(R.string.paying));
        presenter.orderPay();
    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        if(!mBtnCall.isEnabled()){
            //说明已经点了呼叫
            showCanceling();
            presenter.cancelOrder();
        }else{
            //只是显示了路径信息，还没点击呼叫，恢复UI即可
            restoreUI();
        }
    }

    private void restoreUI() {
        //清除地图上所有标记：路径信息，起点，终点
        mLbsLayer.clearAllMarkers();
        //添加定位标记
        addLocationMarker();
        //恢复地图视野
        mLbsLayer.moveCameraToPoint(mStartLocation,17);
        //获取附近的司机
        getNearDrivers(mStartLocation.getLatitude(),mStartLocation.getLongitude());
        //隐藏操作栏
        hideOptArea();
    }

    private void hideOptArea() {
        mOptArea.setVisibility(View.GONE);
        mLoadingArea.setVisibility(View.GONE);
        mBtnCancel.setEnabled(true);
        mBtnCall.setEnabled(true);
    }


    //显示取消中
    private void showCanceling() {
        mTips.setVisibility(View.GONE);
        mLoadingArea.setVisibility(View.VISIBLE);
        mLoadingText.setText(getResources().getString(R.string.canceling));
        mBtnCancel.setEnabled(false);
    }


    /**
     * 呼叫司机
     */
    private void callDriver() {
        if (mIsLogin){
            //已经登陆，直接呼叫
            shwoCalling();
            //请求呼叫
            presenter.callDriver(mPushKey,mCost,mStartLocation,mEndLocation);
        }else{
            //未登陆
            presenter.requestLoginByToken();
            MyLoger.toast(this,"请先登录");
        }
    }

    private void shwoCalling() {
        mTips.setVisibility(View.GONE);
        mLoadingArea.setVisibility(View.VISIBLE);
        mLoadingText.setText(getResources().getString(R.string.calling_driver));
        mBtnCall.setEnabled(false);
        mBtnCancel.setEnabled(true);
    }
    /**
     * 呼叫司机成功
     */
    @Override
    public void showCallDriverSuc(Order order) {
        mLoadingArea.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setText(getResources().getString(R.string.show_call_suc));
        showOptArea();
        //显示路径信息
        if (order.getEndLongitude()!=0||order.getDriverLatitude()!=0){
            mEndLocation = new LocationInfo(order.getEndLatitude(),order.getEndLongitude());
           showRoute(mStartLocation,mEndLocation,new ILbsLayer.OnRouteCompleteListener(){
               @Override
               public void onComplete(RouteInfo result) {
                   MyLoger.d("driverRoute:"+result);
                   mLbsLayer.moveCamera(mStartLocation,mEndLocation);
                   //显示操作区
                   showOptArea();
                   mCost = result.getTaxiCost();
                   String infoString = getResources().getString(R.string.calling);
                   infoString = String.format(infoString,new Float(result.getDistance()).intValue(),
                           mCost,result.getDuration());
                   mTips.setVisibility(View.VISIBLE);
                   mTips.setText(infoString);
                   mBtnCall.setEnabled(false);
               }
           });
        }
    }

    /**
     * 呼叫司机失败
     */
    @Override
    public void showCallDriverFail() {
        mLoadingArea.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setText(getResources().getString(R.string.show_call_fail));
    }

    @Override
    public void showCancelSuc() {
        MyLoger.toast(this,getResources().getString(R.string.order_cancel_suc));
        restoreUI();
    }

    @Override
    public void showCancelFail() {
        MyLoger.toast(this,getResources().getString(R.string.order_cancel_error));
        mBtnCancel.setEnabled(true);
    }

    /**
     * 司机接单
     * @param order
     */
    @Override
    public void showDriverAcceptOrder(final Order order) {
         // 提示信息
        MyLoger.toast(this,getResources().getString(R.string.accept_info));
        //清除标记
        mLbsLayer.clearAllMarkers();
        //司机到乘客之间路线绘制
        final LocationInfo info = new LocationInfo(order.getDriverLatitude(),order.getDriverLongitude());
        showLocationChange(info);
        //显示我的位置
        addLocationMarker();
        //显示操作区
        showOptArea();
        mBtnCall.setEnabled(false);
        //显示司机到乘客的路径
        mLbsLayer.driverRoute(info, mStartLocation, Color.BLUE, new ILbsLayer.OnRouteCompleteListener() {
            @Override
            public void onComplete(RouteInfo result) {
                //地图聚焦到司机和我的位置
                mLbsLayer.moveCamera(mStartLocation,info);
                 //显示司机、路径信息
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("司机：")
                        .append(order.getDriverName())
                        .append(",车牌：")
                        .append(order.getCarNo())
                        .append(",预计")
                        .append(result.getDuration())
                        .append("分钟到达：");
                mTips.setText(stringBuilder.toString());
            }
        });
    }

    /**
     * 司机抵达上车点
     * @param order
     */

    @Override
    public void showDriverArriveStart(Order order) {
        String arriveTem = getResources().getString(R.string.driver_arrive);
        mTips.setText(String.format(arriveTem,order.getDriverName(),order.getCarNo()));
        showOptArea();
        mBtnCall.setEnabled(false);
        mBtnCancel.setEnabled(true);
        mLbsLayer.clearAllMarkers();
        LocationInfo info = new LocationInfo(order.getDriverLatitude(),order.getDriverLongitude());
        showLocationChange(info);
        addLocationMarker();
    }
    /**
     * 司机抵达目的地
     * @param order
     */
    @Override
    public void showArriveEnd(Order order) {
        String tipsTemp = getResources().getString(R.string.pay_info);
        mTips.setText(String.format(tipsTemp,order.getCost(),order.getDriverName(),order.getCarNo()));
        mBtnPay.setVisibility(View.VISIBLE);
        mBtnCall.setVisibility(View.GONE);
        mBtnCancel.setVisibility(View.GONE);
        showOptArea();
    }
    /**
     * 司机开始行程
     * @param order
     */
    @Override
    public void showStartDrive(Order order) {
        mBtnCancel.setVisibility(View.GONE);
        mBtnCall.setVisibility(View.GONE);
        LocationInfo info = new LocationInfo(order.getDriverLatitude(),order.getDriverLongitude());
        //路径规划绘制
        updateDriver2EndRoute(info,order);
    }

    @Override
    public void updateDriver2StartRoute(LocationInfo locationInfo, final Order order) {
        mLbsLayer.clearAllMarkers();
        addLocationMarker();
        showLocationChange(locationInfo);
        mLbsLayer.driverRoute(locationInfo, mStartLocation, Color.GREEN,
                new ILbsLayer.OnRouteCompleteListener() {
                    @Override
                    public void onComplete(RouteInfo result) {
                        String tipsTmp = getResources().getString(R.string.accept_info);
                        mTips.setText(String.format(tipsTmp
                                ,order.getDriverName()
                                ,order.getCarNo()
                                ,result.getDistance()
                                ,result.getDuration()));
                    }
                });
        mLbsLayer.moveCamera(locationInfo,mStartLocation);
    }

    /**
     *
     */
    @Override
    public void showPaySuc(Order order) {
        restoreUI();
        MyLoger.toast(this,getString(R.string.pay_suc));
    }

    @Override
    public void showPayFail() {
        restoreUI();
        MyLoger.toast(this,getString(R.string.pay_fail));
    }

    /**
     * 司机到终点路径绘制或更新
     * @param info
     * @param order
     */
    private void updateDriver2EndRoute(LocationInfo info, final Order order) {
        mLbsLayer.clearAllMarkers();


        if (order.getEndLongitude()!=0||order.getEndLatitude()!=0){
            mEndLocation = new LocationInfo(order.getEndLatitude(),order.getEndLongitude());
        }
        addEndMarker();
        showLocationChange(info);
        mLbsLayer.driverRoute(info, mEndLocation, Color.GREEN,
                new ILbsLayer.OnRouteCompleteListener() {
                    @Override
                    public void onComplete(RouteInfo result) {
                        String tipsTmp = getResources().getString(R.string.dirving);
                        mTips.setText(String.format(tipsTmp
                                ,order.getDriverName()
                                ,order.getCarNo()
                                ,result.getDistance()
                                ,result.getDuration()));
                    }
                });
        mLbsLayer.moveCamera(info,mEndLocation);
    }



    /**
     * 更新POI列表
     * @param results
     */

    private void updatePoiList(final List<LocationInfo> results) {
        List<String> listStr = new ArrayList<>();
        for (int i = 0;i<results.size();i++){
            listStr.add(results.get(i).getName());
        }
        if (mEndAdapter == null ){
            mEndAdapter = new PoiAdapter(getApplicationContext(),listStr);
            mEndEdit.setAdapter(mEndAdapter);
        }else{
            mEndAdapter.setData(listStr);
        }

        mEndEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyLoger.toast(MainActivity.this,results.get(position).getName());
                DevUtil.closeInputMethod(MainActivity.this);
                //记录终点
                mEndLocation = results.get(position);
                //路径规划
                showRoute(mStartLocation, mEndLocation, new ILbsLayer.OnRouteCompleteListener(){
                    @Override
                    public void onComplete(RouteInfo result) {
                        MyLoger.d("driverRoute:"+result);
                        mLbsLayer.moveCamera(mStartLocation,mEndLocation);
                        //显示操作区
                        showOptArea();
                        mCost = result.getTaxiCost();
                        String infoString = getResources().getString(R.string.route_info);
                        infoString = String.format(infoString,new Float(result.getDistance()).intValue(),
                                mCost,result.getDuration());
                        mTips.setVisibility(View.VISIBLE);
                        mTips.setText(infoString);
                    }
                });
            }
        });
        mEndAdapter.notifyDataSetChanged();
    }

    /**
     * 路径绘制
     * @param mStartLocation
     * @param mEndLocation
     */
    private void showRoute(final LocationInfo mStartLocation, final LocationInfo mEndLocation, ILbsLayer.OnRouteCompleteListener listener) {
        mLbsLayer.clearAllMarkers();
        //绘制起点图标
        addStartMarker();
        //绘制终点图标
        addEndMarker();
         //绘制路径
        mLbsLayer.driverRoute(mStartLocation,mEndLocation, Color.GREEN
                ,listener);
    }

    private void showOptArea() {
        mOptArea.setVisibility(View.VISIBLE);

    }
    private void addLocationMarker() {
        if (mLocationBit == null || mLocationBit.isRecycled()){
            mLocationBit = BitmapFactory.decodeResource(getResources()
                    ,R.mipmap.navi_map_gps_locked);
        }
        mLbsLayer.addOrUpdateMarker(mStartLocation,mLocationBit);
    }
    private void addStartMarker() {
        if (mStartBit == null || mStartBit.isRecycled()){
            mStartBit = BitmapFactory.decodeResource(getResources(),R.drawable.start);
        }
        mLbsLayer.addOrUpdateMarker(mStartLocation,mStartBit);
    }

    private void addEndMarker() {
        if (mEndBit == null || mEndBit.isRecycled()){
            mEndBit = BitmapFactory.decodeResource(getResources(),R.drawable.end);
        }
        mLbsLayer.addOrUpdateMarker(mEndLocation,mEndBit);
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
        mIsLogin = true;
        MyLoger.toast(this,"登陆成功");
        getProcessingOrder();
    }

    /**
     * 显示附近的司机
     * @param data
     */
    @Override
    public void showNears(List<LocationInfo> data) {

        for (LocationInfo info : data){
            showLocationChange(info);
        }
    }

    @Override
    public void showLocationChange(LocationInfo locationInfo) {
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

    /**
     * 获取订单信息
     */
    public void getOrderList() {
        presenter.getOrderList();
    }

    /**
     * 获取待处理订单
     * @return
     */
    public void getProcessingOrder() {
        /**
         * 满足：已经登陆、已经定位成功
         */
        if (mIsLogin&&mIsLocate){
            presenter.getProcessingOrder();
        }
    }
}
