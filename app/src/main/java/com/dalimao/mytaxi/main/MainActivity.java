package com.dalimao.mytaxi.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.pressenter.IMainActivityPresenter;
import com.dalimao.mytaxi.account.pressenter.MainActivityPresenterImpl;
import com.dalimao.mytaxi.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.util.MyLoger;

/**
 * author: apple
 * created on: 2018/5/15 上午9:52
 * description:
 * 1 检查本地记录（登录状态检查）
 * 2 若用户没有登陆则登陆
 * 3 登陆之前先校验手机号
 * 4 token 有效使用 token 自动登录
 * todo 地图初始化
 */
public class MainActivity extends AppCompatActivity implements IMainAcitivityView{
    IMainActivityPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       presenter = new MainActivityPresenterImpl(this);
        presenter.requestLoginByToken();
        RxBus.getInstance().register(presenter);
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
    }
}
