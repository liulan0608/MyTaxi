package com.dalimao.mytaxi;

import com.dalimao.mytaxi.common.databus.IDataBusSubscriber;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.databus.RxBus;
import com.dalimao.mytaxi.common.util.MyLoger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.functions.Func1;

/**
 * author: apple
 * created on: 2018/6/29 下午5:27
 * description:
 */
public class RxBusTest {
    private final static String TAG = "RxBusTest";
    Presenter presenter;

    @Before
    public void setUp()throws  Exception{
        /**
         * 初始化 presenter 并注册
         */
        presenter = new Presenter(new Manager());
        RxBus.getInstance().register(presenter);
    }
    @After
    public void tearDown(){
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RxBus.getInstance().unRegister(presenter);
    }
    @Test
    public void testGetUser() throws Exception{
        presenter.getUser();
    }
    /**
     * 模拟presenter
     */
    class Presenter  {
        private Manager manager;

        public Presenter(Manager manager) {
            this.manager = manager;
        }
        public void getUser(){
            manager.getUser();
        }
        public void getOrder(){
            manager.getOrder();
        }

        @RegisterBus
        public void onUser(User user){
            MyLoger.d(TAG,"receiver user in thread:"+Thread.currentThread().getName());
        }
        @RegisterBus
        public void onOrder(Order order){
            MyLoger.d(TAG,"receiver order in thread:"+Thread.currentThread().getName());
        }
    }
    /**
     * 模拟MODEL
     */
    class Manager{
        public void getUser(){
            RxBus.getInstance().chainProcess(new Func1() {
                @Override
                public Object call(Object o) {
                    MyLoger.d(TAG,"chainProcess getUser start in thread:"+
                    Thread.currentThread().getName());
                    User user = new User();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //把数据传到presenter
                    return user;
                }
            });
        }
        public void getOrder(){
            RxBus.getInstance().chainProcess(new Func1() {
                @Override
                public Object call(Object o) {
                    MyLoger.d(TAG,"chainProcess getOrder start in thread:"+
                            Thread.currentThread().getName());
                    Order order = new Order();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //把数据传到presenter
                    return order;
                }
            });
        }
    }


    /**
     * 返回的数据
     */
    class User{

    }
    class Order{

    }
}
