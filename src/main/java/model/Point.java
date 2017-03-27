package model;

import java.sql.Timestamp;

/**
 * 作业点模型
 */
public class Point {
    private int depth;
    private int workWidth;
    private double latitude;
    private double longitude;
    private Timestamp gpsTime;

    public Point() {}



    public Point(Timestamp gpsTime, double latitude, double longitude, int depth, int workWidth) {
        this.gpsTime = gpsTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
        this.workWidth = workWidth;
    }

    public double getLatitude (){
        return latitude;
    }

    public double getLongitude (){
        return longitude;
    }

    public Timestamp getGpsTime () {
        return gpsTime;
    }

    public int getDepth () {
        return depth;
    }

    public int getWorkWidth () {
        return workWidth;
    }

}
