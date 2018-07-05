package com.dalimao.mytaxi.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.pressenter.ILoginDialogPresenter;
import com.dalimao.mytaxi.account.pressenter.LoginDialogPresenterImpl;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.util.MyLoger;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class LoginDialog extends Dialog implements ILoginDialogView{
    private String mPhone;
    private TextView tv_phone;
    private EditText et_pw;
    private Button btn_confirm;
    private TextView tv_tips;
    private ProgressBar mLoading;
    private View mRoot;

    private ILoginDialogPresenter presenter;
    @Override
    public void showLoading() {

    }
    @Override
    public void showError(int Code, String msg) {
        showOrHideLoading(false);
        tv_tips.setVisibility(View.VISIBLE);
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        switch (Code){
            case IAccountManager.SERVER_FAIL:
                tv_tips.setText("服务器繁忙");
                break;
            case IAccountManager.PASSWORD_ERROR:
                tv_tips.setText("密码错误");
                break;
        }
    }
    public LoginDialog(Context context,String phone) {
        this(context, R.style.Dialog);
        this.mPhone = phone;
        presenter = new LoginDialogPresenterImpl(this);
    }

    public LoginDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.dialog_login,null);
        setContentView(mRoot);
        initLinstener();
// 注册 presenter
        RxBus.getInstance().register(presenter);
    }

    @Override
    public void dismiss() {
        super.dismiss();
         //注销presenter
        RxBus.getInstance().unRegister(presenter);
    }

    private void initLinstener() {
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        et_pw = (EditText) findViewById(R.id.pw);
        tv_phone = (TextView) findViewById(R.id.phone);
        tv_phone.setText(mPhone);
        tv_tips = (TextView) findViewById(R.id.tips);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        //手机号输入框监听手机号输入是否合法
       findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dismiss();
           }
       });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    /**
     *  提交登录
     */
    private void submit() {
        presenter.requestLogin(mPhone,et_pw.getText().toString());

    }
    /**
     * 显示／隐藏 loading
     */
    public void showOrHideLoading(boolean show){
        if (show){
            mLoading.setVisibility(View.VISIBLE);
            btn_confirm.setVisibility(View.GONE);
        }else{
            mLoading.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理登录成功 ui
     */
    @Override
    public void showLoginSuc(){
        mLoading.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.GONE);
        tv_tips.setVisibility(View.VISIBLE);
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        tv_tips.setText("登录成功");
        MyLoger.toast(getContext(),"登录成功");
        dismiss();
    }
}
