package com.iyuba.music.activity.eggshell.weight_monitor;

import java.util.Date;

/**
 * Created by chentong1 on 2017/6/6.
 */

public class WeightMonitorEntity {
    private Date time;
    private double weight;
    private double change;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public WeightMonitorEntity(Date time, double weight, double change) {
        this.time = time;
        this.weight = weight;
        this.change = change;
    }

    public WeightMonitorEntity() {
    }
}
