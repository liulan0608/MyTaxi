package com.dalimao.mytaxi.main.model.response;

/**
 * author: apple
 * created on: 2018/10/25 下午4:16
 * description:
 */
public class Order {
    // "carNo":"粤B88888",
// "cost":"61.90875",
// "driverLatitude":30.183263,
// "driverLongitude":120.216239,
// "driverName":"大利猫(http:\/\/www.liuguangli.win)",
// "driverPhone":"15988888888",
// "driverRotation":25.39,
// "key":"3136ff94-b6f8-4930-8300-b8132e50486e",
// "orderId":"601e3d54ea",
// "state":1},"type":2}
    private float cost;
    private int state;
    private String orderId;
    private String uid;
    private String key;
    private String phone;
    private double driverLatitude;
    private double driverLongitude;
    private double driverRotation;
    private String driverName;
    private String carNo;
    private String driverPhone;
    private String startAddr;
    private String endAddr;

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getDriverLatitude() {
        return driverLatitude;
    }

    public void setDriverLatitude(double driverLatitude) {
        this.driverLatitude = driverLatitude;
    }

    public double getDriverLongitude() {
        return driverLongitude;
    }

    public void setDriverLongitude(double driverLongitude) {
        this.driverLongitude = driverLongitude;
    }

    public double getDriverRotation() {
        return driverRotation;
    }

    public void setDriverRotation(double driverRotation) {
        this.driverRotation = driverRotation;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(String startAddr) {
        this.startAddr = startAddr;
    }

    public String getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(String endAddr) {
        this.endAddr = endAddr;
    }
}
