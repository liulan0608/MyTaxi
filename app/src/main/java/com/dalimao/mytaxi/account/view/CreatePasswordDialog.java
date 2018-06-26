package com.dalimao.mytaxi.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.pressenter.CreatePasswordDialogPresenterImpl;
import com.dalimao.mytaxi.account.pressenter.ICreatePassordDialogPresenter;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class CreatePasswordDialog extends Dialog implements ICreatePasswordDialogView{

    private static final String TAG = "CreatePasswordDialog";
    private TextView tv_title;
    private TextView tv_phone;
    private EditText ed_pw;
    private EditText ed_pw1;
    private Button btn_confirm;
    private ProgressBar mLoading;
    private TextView tv_tips;
    private String mPhoneStr;

    private ICreatePassordDialogPresenter passwordPresenter;


    /**
     * 登陆成功
     */
    @Override
    public void showLoginSuc() {
        dismiss();
        Toast.makeText(getContext(),"登陆成功",Toast.LENGTH_LONG).show();
    }

    public CreatePasswordDialog(Context context,String phone) {
        super(context,R.style.Dialog);
        mPhoneStr = phone;
        passwordPresenter = new CreatePasswordDialogPresenterImpl(this);
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
         String pasword = ed_pw.getText().toString();
        if (checkPassword(pasword)){
            //提交注册
            passwordPresenter.requestRegister(mPhoneStr,pasword);
        }
    }

    /**
     * 检查密码输入
     */
    private boolean checkPassword(String password){
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
    @Override
    public void showRegisterSuc() {
        mLoading.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.GONE);
        tv_tips.setVisibility(View.VISIBLE);
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        tv_tips.setText("注册成功，正在为您自动登录");
        //自动登录
        passwordPresenter.requestLogin(mPhoneStr,ed_pw.getText().toString());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError(int Code, String msg) {
        tv_tips.setText("服务器繁忙");
        tv_tips.setTextColor(getContext().getResources().getColor(R.color.error_red));
    }
}
