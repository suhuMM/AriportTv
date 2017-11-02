package com.example.phone;

/**
 * Created by suhu on 2017/8/4.
 */

public final class Latitude {
    public double x;
    public double y;
    public int typ;

    public Latitude(){}

    public Latitude(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Latitude(double x, double y, int typ) {
        this.x = x;
        this.y = y;
        this.typ = typ;
    }
}
