package com.dalimao.mytaxi.account.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.common.util.FormaUtil;

/**
 * author: apple
 * created on: 2018/5/15 上午10:03
 * description:
 */
public class PhoneInputDialog extends Dialog {
    private View mRoot;
    private EditText mPhone;
    private Button mButton;

    public PhoneInputDialog( Context context) {
        this(context, R.style.Dialog);
    }

    public PhoneInputDialog( Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.dialog_phone_input,null);
        setContentView(mRoot);
        initLinstener();
    }

    private void initLinstener() {
        mButton = (Button) findViewById(R.id.btn_next);
        mButton.setEnabled(false);
        mPhone = (EditText) findViewById(R.id.phone);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //手机号输入框监听手机号输入是否合法
        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                check();
            }

            
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String phone = mPhone.getText().toString();
                // TODO: 2018/5/15 显示输入验证码的输入框
                SmsCodeDialog smsCodeDialog=new SmsCodeDialog(getContext(),phone);
                smsCodeDialog.show();
            }
        });
    }

    private void check() {
        String phone = mPhone.getText().toString();
        boolean legal = FormaUtil.checkMobile(phone);
        mButton.setEnabled(legal);
    }
}
