package com.dalimao.mytaxi.account;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.bean.Login;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.BaseResponse;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.google.gson.Gson;

import java.lang.ref.SoftReference;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class SmsCodeDialog extends Dialog {
    private static final String Tag = "SmsCodeDialog";
    private static final int SMS_SEND_SUCCESS = 1;
    private static final int SMS_SEND_FAIL = -1;
    private static final int SMS_CHECK_SUCCESS = 2;
    private static final int SMS_CHECK_FAIL = -2;
    private static final int USER_EXIST = 3;
    private static final int USER_NOT_EXIST = -3;
    private static final int SMS_SERVER_FAIL = 100;

    private String mPhone;
    private Button mResentBtn;//重新发送按钮
    private VerificationCodeInput mVerificationInput; //验证码输入框
    private View mLoading;
    private View mErrorView;
    private TextView mPhoneTv;//手机号码显示框


    private IHttpClient mHttpClient;
    private MyHandler mHandler;

    /**
     * 验证码倒计时
     * @param context
     */
    private CountDownTimer mCountDownTimer = new CountDownTimer(10000,1000){

        @Override
        public void onTick(long millisUntilFinished) {
            mResentBtn.setEnabled(false);
            mResentBtn.setText(String.format("%s秒后重新发送",millisUntilFinished/1000));
        }
        @Override
        public void onFinish() {
            mResentBtn.setEnabled(true);
            mResentBtn.setText("重新发送");
            cancel();
        }
    };
    /**
     * 接收子线程消息的 Handler
     */
    static class MyHandler extends Handler{
        //软引用
        SoftReference<SmsCodeDialog> codeDialogRef;

        public MyHandler(SmsCodeDialog codeDialog) {
            codeDialogRef = new SoftReference<SmsCodeDialog>(codeDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            SmsCodeDialog dialog = codeDialogRef.get();
            if (dialog == null){
                return;
            }
            switch (msg.what){
                case SMS_SEND_SUCCESS:
                    dialog.mCountDownTimer.start();
                    break;
                case SMS_SEND_FAIL:
                    Toast.makeText(dialog.getContext(),dialog.getContext().getString(R.string.sms_send_fail),Toast.LENGTH_LONG).show();
                    break;
                case SMS_CHECK_SUCCESS:
                dialog.showVerifyState(true);
                    break;
                case SMS_CHECK_FAIL:
                    //验证码成功
                    dialog.showVerifyState(false);
                    break;

                case USER_EXIST:
                    //用户存在
                    dialog.showUserExist(true);
                    break;
                case USER_NOT_EXIST:
                    //用户不存在
                    dialog.showUserExist(false);
                    break;
                case SMS_SERVER_FAIL:
                    //服务器异常
                    Toast.makeText(dialog.getContext(),
                            dialog.getContext().getString(R.string.error_server_msg),Toast.LENGTH_LONG).show();
                    break;


            }

        }
    }

    public SmsCodeDialog(Context context,String phone) {
        super(context);
        this.mPhone = phone;
        mHttpClient= new OkHttpClientImpl();
        mHandler=new MyHandler(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_smscode_input,null);
        setContentView(root);
        mPhoneTv = (TextView) findViewById(R.id.phone);
        String template = "正在向%s发送短信验证码";
        mPhoneTv.setText(String.format(template,mPhone));
        mResentBtn = (Button) findViewById(R.id.btn_resend);
        mVerificationInput = (VerificationCodeInput) findViewById(R.id.verificationCodeInput);
        mLoading = findViewById(R.id.loading);
        mErrorView = findViewById(R.id.error);
        mErrorView.setVisibility(View.GONE);
        initLinstener();
        requestSendSmsCode();
    }

    /**
     * 请求下发验证码
     */
    private void requestSendSmsCode() {
        new Thread(){
            @Override
            public void run() {
                String url = API.GET_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",mPhone);
                IResponse response = mHttpClient.get(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson()
                            .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                    mHandler.sendEmptyMessage(SMS_SEND_SUCCESS);
                }else{
                    mHandler.sendEmptyMessage(SMS_SEND_FAIL);
                }
                }else{
                    mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                }
            }
        }.start();



    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCountDownTimer.cancel();
    }

    private void initLinstener() {
        //关闭按钮 注册监听器
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mResentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend();
            }
        });
        mVerificationInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String s) {
                commit(s);
            }
        });
    }
    private void commit(final String code){
        showLoading();
        /**
         * 网络请求 校验验证码
         */
        new Thread(){
            @Override
            public void run() {
                String url = API.CHECK_SMS_CODE;
                IRequest request = new BaseRequest(url);
                request.setBody("phone",mPhone);
                request.setBody("code",code);
                IResponse response = mHttpClient.get(request,false);
                if (response.getCode() == BaseResponse.STATE_OK){
                        BaseBizResponse bizRes = new Gson()
                                .fromJson(response.getData()
                                    ,BaseBizResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK){
                        mHandler.sendEmptyMessage(SMS_CHECK_SUCCESS);
                    }else{
                        mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                    }
                }else{
                    mHandler.sendEmptyMessage(SMS_CHECK_FAIL);
                }
            }
        }.start();

    }

    private void resend() {
        String template = "正在向%s发送短信验证码";
//        String  template = "重新发送";
    }
    public void showLoading(){
        mLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 验证码状态判读
     * @param suc
     */
    public void showVerifyState(boolean suc){
        if (!suc){
            //提示验证码错误
            mErrorView.setVisibility(View.VISIBLE);
            mVerificationInput.setEnabled(true);
            mLoading.setVisibility(View.GONE);
        }else{
            mErrorView.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
            // 检查用户是否存在
            new Thread(){
                @Override
                public void run() {
                    String url = API.CHECK_USER_EXIST;
                    IRequest request = new BaseRequest(url);
                    request.setBody("phone",mPhone);
                    IResponse response = mHttpClient.get(request,false);
                    if (response.getCode() == BaseResponse.STATE_OK){
                        BaseBizResponse bizRes = new Gson()
                                .fromJson(response.getData()
                                        ,BaseBizResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_USER_EXIST){
                            mHandler.sendEmptyMessage(USER_EXIST);
                        }else if (bizRes.getCode() == BaseBizResponse.STATE_USER_NOT_EXIT){
                            mHandler.sendEmptyMessage(USER_NOT_EXIST);
                        }
                    }else{
                        mHandler.sendEmptyMessage(SMS_SERVER_FAIL);
                    }
                }
            }.start();
        }
    }

        public void showUserExist(boolean exist){
            mLoading.setVisibility(View.GONE);
            mErrorView.setVisibility(View.GONE);
            dismiss();
            if (!exist){
                // 用户不存在，进入注册
                CreatePasswordDialog dialog = new CreatePasswordDialog(getContext(),mPhone);
                dialog.show();
            } else {
                // 用户存在，进入登录
                LoginDialog dialog = new LoginDialog(getContext(),mPhone);
                    dialog.show();
            }
        }
}
