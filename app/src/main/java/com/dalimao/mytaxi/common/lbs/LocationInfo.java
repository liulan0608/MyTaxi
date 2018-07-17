package com.dalimao.mytaxi.common.lbs;

/**
 * author: apple
 * created on: 2018/7/9 下午5:48
 * description:
 */
public class LocationInfo {
    private String  key;
    private String name;
    private double latitude;
    private double longitude;
    private float rotation;

    public LocationInfo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "key=" + getKey() +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", rotation=" + rotation +
                '}';
    }
}
