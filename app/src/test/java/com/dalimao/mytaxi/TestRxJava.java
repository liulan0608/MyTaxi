package com.dalimao.mytaxi;

import android.util.Log;

import com.dalimao.mytaxi.common.util.MyLoger;

import org.junit.Test;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author: apple
 * created on: 2018/6/29 上午11:44
 * description:
 */
public class TestRxJava {
    @Test
    public void testSubscribe(){
        //观察者／订阅者
        Observer<String> observer =
                new Observer<String>() {
            @Override
            public void onCompleted() {
                MyLoger.system("onCompleted in thread:"+Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                MyLoger.system("onError in thread:"+Thread.currentThread().getName());
            }

            @Override
            public void onNext(String s) {
                MyLoger.system("onNext in thread:"+Thread.currentThread().getName());
                MyLoger.system(s);
            }
        };
        //被观察者
        Observable<String> observable = Observable.create(
                new Observable.OnSubscribe<String>(){
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        //发生事件
                        MyLoger.system("call in thread:"+Thread.currentThread().getName());
                        subscriber.onNext("你好");
//                        subscriber.onError(new Exception("error"));
                        subscriber.onNext("大家好");
                        subscriber.onCompleted();
                    }
        });
         //订阅
        observable.subscribeOn(Schedulers.io())//指定生产事件在io线程
                .observeOn(Schedulers.newThread())//指定消费事件在新线程
                .subscribe(observer);
    }

    @Test
    public void testMap(){
        String name = "liulan";
        Observable.just(name).observeOn(Schedulers.newThread())
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
        }).observeOn(Schedulers.newThread())
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
