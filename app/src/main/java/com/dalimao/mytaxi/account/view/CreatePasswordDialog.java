package com.dalimao.mytaxi.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.response.Login;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
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
import com.google.gson.Gson;

import java.lang.ref.SoftReference;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class CreatePasswordDialog extends Dialog {

    private static final String TAG = "CreatePasswordDialog";
    private static final int REGISTER_SUC = 1;
    private static final int SERVER_FAIL = 100;
    private static final int PASSWORD_ERROR = 100005;
    private static final int LONGIN_SUC = 2;

    private TextView tv_title;
    private TextView tv_phone;
    private EditText ed_pw;
    private EditText ed_pw1;
    private Button btn_confirm;
    private ProgressBar mLoading;
    private TextView tv_tips;
    private IHttpClient mHttpClient;
    private String mPhoneStr;
    private MyHandler mHandler;

    /**
     * 接收子线程消息的Handler
     */
    static class MyHandler extends Handler{
        SoftReference<CreatePasswordDialog> codeDialogRef;

        public MyHandler(CreatePasswordDialog codeDialog) {
            codeDialogRef = new SoftReference<CreatePasswordDialog>(codeDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            CreatePasswordDialog dialog = codeDialogRef.get();
            if (dialog == null){
                return;
            }
            //处理UI变化
            switch (msg.what){
                case REGISTER_SUC:
                    //注册成功
                    dialog.showRegisterSuc();
                    break;
                case LONGIN_SUC:
                    dialog.showLoginSuc();
                    break;
                case SERVER_FAIL:
                    //注册不成功
                    dialog.showServerError();
                    break;

            }
        }
    }

    /**
     * 登陆成功
     */
    private void showLoginSuc() {
        dismiss();
        Toast.makeText(getContext(),"登陆成功",Toast.LENGTH_LONG).show();
    }

    public CreatePasswordDialog(Context context,String phone) {
        super(context,R.style.Dialog);
        mPhoneStr = phone;
        mHttpClient = new OkHttpClientImpl();
        mHandler = new MyHandler(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_create_pw,null);
        setContentView(root);
        initViews();

    }

    private void initViews() {
        tv_phone = (TextView) findViewById(R.id.phone);
        ed_pw = (EditText) findViewById(R.id.pw);
        ed_pw1 = (EditText) findViewById(R.id.pw1);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        tv_tips = (TextView) findViewById(R.id.tips);
        tv_title = (TextView) findViewById(R.id.dialog_title);
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
        tv_phone.setText(mPhoneStr);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
    private void submit() {
        if (checkPassword()){
            final String pasword = ed_pw.getText().toString();
            final String phone = mPhoneStr;
            //请求网络，提交注册

        }
    }




    /**
     * 检查密码输入
     */
    private boolean checkPassword(){
        String password = ed_pw.getText().toString();
        if (TextUtils.isEmpty(password)){
            tv_tips.setVisibility(View.VISIBLE);
            tv_tips.setText("密码不能为空");
            return false;
        }
        if (!password.equals(ed_pw1.getText().toString())){
            tv_tips.setText("两次输入的密码不一致");
            tv_tips.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    /**
     * 处理注册成功
     */
    private void showRegisterSuc() {
        mLoading.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.GONE);
        tv_tips.setVisibility(View.VISIBLE);
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        tv_tips.setText("注册成功，正在为您自动登录");
        //todo： 请求网络，完成自动登录


    }

    private void showServerError() {
        tv_tips.setText("服务器繁忙");
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.error_red));

    }

}
