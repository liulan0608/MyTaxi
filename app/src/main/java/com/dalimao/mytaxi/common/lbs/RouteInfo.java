package com.dalimao.mytaxi.common.lbs;

/**路线info
 * author: apple
 * created on: 2018/10/22 下午5:45
 * description:
 */
public class RouteInfo {
    //两点之间的距离
    private float distance;
    //预计价格
    private float taxiCost;
    //预计行车时间
    private int duration;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getTaxiCost() {
        return taxiCost;
    }

    public void setTaxiCost(float taxiCost) {
        this.taxiCost = taxiCost;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
