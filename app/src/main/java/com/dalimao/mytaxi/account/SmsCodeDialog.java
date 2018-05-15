package com.dalimao.mytaxi.account;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dalimao.corelibrary.VerificationCodeInput;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.biz.BaseBizResponse;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.IRequest;
import com.dalimao.mytaxi.common.http.IResponse;
import com.dalimao.mytaxi.common.http.api.API;
import com.dalimao.mytaxi.common.http.impl.BaseRequest;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.util.MyLoger;
import com.google.gson.Gson;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class SmsCodeDialog extends Dialog {
    private static final String Tag = "SmsCodeDialog";
    private String mPhone;
    private Button mResentBtn;//重新发送按钮
    private VerificationCodeInput mVerificationInput; //验证码输入框
    private View mLoading;
    private View mErrorView;
    private TextView mPhoneTv;//手机号码显示框


    private IHttpClient mHttpClient;

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
    public SmsCodeDialog(Context context,String phone) {
        super(context);
        this.mPhone = phone;
        mHttpClient= new OkHttpClientImpl();
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
        // TODO: 2018/5/15 请求下发验证码
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
                if (response.getCode() == BaseBizResponse.STATE_OK){
                    BaseBizResponse bizRes = new Gson().fromJson(response.getData(),BaseBizResponse.class);
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
    private void commit(String code){
        showLoading();
        // TODO: 2018/5/15 网络请求校验验证码

    }

    private void resend() {
        String template = "正在向%s发送短信验证码";
//        String  template = "重新发送";
    }
    public void showLoading(){
        mLoading.setVisibility(View.VISIBLE);
    }
}
