package model;

public class TimeUnit {
    private double pointSize;
    private double time;

    public TimeUnit(){}

    public TimeUnit(double pointSize, double time){
        this.pointSize = pointSize;
        this.time = time;
    }

    public double getPointSize() {
        return pointSize;
    }

    public void setPointSize(int pointSize) {
        this.pointSize = pointSize;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
