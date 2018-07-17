package com.dalimao.mytaxi.splash;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.main.view.MainActivity;

/**
 * Created by vigroid on 11/13/17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Drawable drawable = this.getDrawable(R.drawable.anim);

            final AnimatedVectorDrawable anim =
                    (AnimatedVectorDrawable) drawable;
            final ImageView logo = (ImageView) findViewById(R.id.logo);
            logo.setImageResource(R.drawable.anim);
            anim.start();
        }
        /**
         * 延时3秒跳转到 main 页面
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },3000);
    }

}
