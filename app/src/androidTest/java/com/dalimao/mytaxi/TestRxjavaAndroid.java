package com.dalimao.mytaxi;

import android.support.test.runner.AndroidJUnit4;

import com.dalimao.mytaxi.common.util.MyLoger;

import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author: apple
 * created on: 2018/6/29 下午2:41
 * description:
 */
@RunWith(AndroidJUnit4.class)
public class TestRxjavaAndroid {
    @Test
    public void testMapAnndroid(){
        String name = "liulan";
        Observable.just(name).subscribeOn(Schedulers.newThread())
                .map(new Func1<String, User>() {

                    @Override
                    public User call(String s) {
                        User user = new User();
                        user.setName(s);
                        MyLoger.system("process User call in the thread:"+Thread.currentThread().getName());
                        return user;
                    }
                }).observeOn(Schedulers.newThread())
                .map(new Func1<User, Object>() {
                    @Override
                    public Object call(User user) {
                        MyLoger.system("process1 User call in the thread:"+Thread.currentThread().getName());
                        return user;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        User user = (User) o;
                        MyLoger.system(user.getName());
                        MyLoger.system("receive User call in the thread:"+Thread.currentThread().getName());
                    }
                });
    }

    private class User {
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
