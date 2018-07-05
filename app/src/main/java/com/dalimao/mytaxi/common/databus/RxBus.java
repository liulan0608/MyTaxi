package com.dalimao.mytaxi.common.databus;

import com.dalimao.mytaxi.common.util.MyLoger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author: apple
 * created on: 2018/6/29 下午3:12
 * description:
 */
public class RxBus {
    private static final String TAG = "RxBus";
    private static volatile RxBus instance;

    //订阅者集合
    private Set<Object> subscribers;
    /**
     * 注册IDataBusSubscriber
     */
    public synchronized void register(Object subscriber){
        subscribers.add(subscriber);
    }
    /**
     * 注销IDataBusSubscriber
     */
    public synchronized void unRegister(Object subscriber){
        subscribers.remove(subscriber);
    }
    /**
     * 单例模式
     */
    private RxBus(){
        subscribers = new CopyOnWriteArraySet<>();
    }
    public static synchronized RxBus getInstance(){
        if (instance == null){
            synchronized (RxBus.class){
                if (instance == null){
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }
/**
 * 包装处理过程
 * @param func
 */
    public void chainProcess(Func1 func){
        Observable.just("")
                .subscribeOn(Schedulers.io())//指定处理过程在 IO 线程
                .map(func)//包装处理过程
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费在Main线程
                .subscribe(new Action1() {
                    @Override
                    public void call(Object data) {
                        MyLoger.d(TAG,"chainProcess start");
                        for (Object s: subscribers)
                            //数据发送到注册的 Subscriber
                            callMethodAnnotiation(s,data);
                      }
                });
    }

    /**
     * 反射获取对象方法列表，判断：
     * 1、是否被注解修饰
     * 2、参数类型是否和 data 类型一直
     * @param target
     * @param data
     */
    private void callMethodAnnotiation(Object target, Object data) {
        Method[] methodArray = target.getClass().getDeclaredMethods();
        for (int i = 0; i<methodArray.length; i++){
            try {
                if(methodArray[i].isAnnotationPresent(RegisterBus.class)){
                    //被@RegisterBus修饰的方法
                    Class paramType = methodArray[i].getParameterTypes()[0];
                    if (data.getClass().getName().equals(paramType.getName())){
                        //参数类型和data 一样，调用此方法
                        methodArray[i].invoke(target,new Object[]{data});
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


}