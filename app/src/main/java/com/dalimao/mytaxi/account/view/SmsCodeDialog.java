package com.dalimao.mytaxi.account.view;

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
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.pressenter.ISmsCodeDialogPresenter;
import com.dalimao.mytaxi.account.pressenter.SmsCodeDialogPresenterImpl;
import com.dalimao.mytaxi.common.util.MyLoger;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class SmsCodeDialog extends Dialog implements ISmsCodeDialogView{
    private static final String Tag = "SmsCodeDialog";
    private String mPhone;
    private Button mResentBtn;//重新发送按钮
    private VerificationCodeInput mVerificationInput; //验证码输入框
    private View mLoading;
    private View mErrorView;
    private TextView mPhoneTv;//手机号码显示框

    private ISmsCodeDialogPresenter mPresenter;

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

        mPresenter = new SmsCodeDialogPresenterImpl(this);
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
        mPresenter.requestSendSmsCode(mPhone);
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

    private void resend() {
        String template = getContext().getString(R.string.sending);
        mPhoneTv.setText(String.format(template,mPhone));
    }

    private void commit(String code){
        mPresenter.requestCheckSmsCode(mPhone,code);

    }

    @Override
    public void showLoading(){
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(int Code, String msg) {
        mLoading.setVisibility(View.GONE);
        switch (Code){
            case IAccountManager.SMS_SEND_FAIL:
                MyLoger.toast(getContext(),getContext().getString(R.string.sms_send_fail));
                break;
            case IAccountManager.SMS_CHECK_FAIL:
                //提示验证码错误
                mErrorView.setVisibility(View.VISIBLE);
                mVerificationInput.setEnabled(true);
                break;
            case IAccountManager.SERVER_FAIL:
                MyLoger.toast(getContext(),getContext().getString(R.string.error_server_msg));

                break;

        }

    }
    @Override
    public void showCountDownTimer() {
        //启动倒计时
        mPhoneTv.setText(String.format(getContext()
                .getString(R.string.sms_send_code),mPhone));
        mCountDownTimer.start();
        mResentBtn.setEnabled(false);
    }
    /**
     * 验证码状态判读
     * @param suc
     */
    @Override
    public void showSmsCodeCheckState(boolean suc) {
        if (!suc){
            //提示验证码错误
            mErrorView.setVisibility(View.VISIBLE);
            mVerificationInput.setEnabled(true);
            mLoading.setVisibility(View.GONE);
        }else{
            mErrorView.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);
            // 检查用户是否存在
            mPresenter.requestCheckUserExist(mPhone);
        }
    }
    @Override
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
