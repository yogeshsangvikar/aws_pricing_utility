package com.my.aws.pojo;

import java.util.Date;

/**
 * Created by Yogesh.Sangvikar on 5/13/2015.
 */
public class EC2InstancePrice {

    private String region;
    private String instanceType;
    private String os;
    private double price;
    private Date lastupdated;

    public EC2InstancePrice() {
    }

    public EC2InstancePrice(String instanceType, String os, double price, String region) {
        this.instanceType = instanceType;
        this.os = os;
        this.price = price;
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public Date getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(Date lastupdated) {
        this.lastupdated = lastupdated;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "EC2InstancePrice{" +
                ", region='" + region + '\'' +
                ", instanceType='" + instanceType + '\'' +
                ", os='" + os + '\'' +
                ", price=" + price +
                ", lastupdated=" + lastupdated +
                '}';
    }
}
