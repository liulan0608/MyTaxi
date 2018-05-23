package com.dalimao.mytaxi.account;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.bean.Login;
import com.dalimao.mytaxi.account.response.LoginResponse;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.DevUtil;
import com.dalimao.mytaxi.common.util.FormaUtil;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.lang.ref.SoftReference;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class LoginDialog extends Dialog {
    private static final int LONGIN_SUC = 1;
    private static final int SERVER_FAIL = 2;
    private static final int PASSWORD_ERROR = 3;

    IHttpClient mHttpClient;
    private String mPhone;
    private TextView tv_phone;
    private EditText et_pw;
    private Button btn_confirm;
    private TextView tv_tips;
    private ProgressBar mLoading;
    private MyHandler mHandler;

    private View mRoot;
    /**
     * 接收子线程消息的Handler
     */
    static class MyHandler extends Handler {
        SoftReference<LoginDialog> codeDialogRef;

        public MyHandler(LoginDialog codeDialog) {
            codeDialogRef = new SoftReference<LoginDialog>(codeDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginDialog dialog = codeDialogRef.get();
            if (dialog == null){
                return;
            }
            //处理UI变化
            switch (msg.what){
                case LONGIN_SUC:
                    //登录成功
                    dialog.showLoginSuc();
                    break;
                case PASSWORD_ERROR:
                    //密码错误
                    dialog.showPasswordError();
                    break;
                case SERVER_FAIL:
                    //注册不成功
                    dialog.showServerError();
                    break;

            }
        }



    }
    public LoginDialog(Context context,String phone) {
        this(context, R.style.Dialog);
        this.mPhone = phone;
        mHttpClient= new OkHttpClientImpl();
        mHandler=new MyHandler(this);
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
       
        //  网络请求登录
        new Thread(){
            @Override
            public void run() {
                String url= API.LOGIN_PHONE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",tv_phone.getText().toString());
                request.setBody("password",et_pw.getText().toString());
                IResponse response = mHttpClient.post(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                    LoginResponse loginRes = new Gson().fromJson(response.getData(),LoginResponse.class);
                    if (loginRes.getCode() == BaseBizResponse.STATE_OK){
                        //保存登陆信息
                        Login login = loginRes.getData();
                        SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance()
                                ,SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT,login);
                        mHandler.sendEmptyMessage(LONGIN_SUC);
                    }else if (loginRes.getCode() == BaseBizResponse.STATE_PW_ERROR){
                        mHandler.sendEmptyMessage(PASSWORD_ERROR);
                    }
                }else {
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }

            }
        }.start();
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
    public void showLoginSuc(){
        mLoading.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.GONE);
        tv_tips.setVisibility(View.VISIBLE);
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        tv_tips.setText("登录成功");
        MyLoger.toast(getContext(),"登录成功");
        dismiss();
    }

    /**
     * 显示服务器出错
     */
    public void showServerError(){
        showOrHideLoading(false);
        tv_tips.setVisibility(View.VISIBLE);
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        tv_tips.setText("服务器繁忙");
    }
    /**
     * 密码错误
     */
    public void showPasswordError(){
        showOrHideLoading(false);
        tv_tips.setVisibility(View.VISIBLE);
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        tv_tips.setText("密码错误");
    }
}
