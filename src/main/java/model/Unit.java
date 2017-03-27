package model;

/**
 * 分类设备单元
 */
public class Unit {
    private String deviceID;
    private double gridArea;
    private double repeatedRate;
    private double unqualifiedRate;
    private double unitMileage;
    private int blockSize;
    private double centerSpacing;
    private double graphicDegree;
    private double leakageRate;
    private double score;
    private int label;

    public Unit(){}
    public Unit(String deviceID, double gridArea, double repeatedRate, double unqualifiedRate, double unitMileage, int blockSize, double centerSpacing, double graphicDegree, double leakageRate){
        this.deviceID = deviceID;
        this.gridArea = gridArea;
        this.repeatedRate = repeatedRate;
        this.unqualifiedRate = unqualifiedRate;
        this.unitMileage = unitMileage;
        this.blockSize = blockSize;
        this.centerSpacing = centerSpacing;
        this.graphicDegree = graphicDegree;
        this.leakageRate = leakageRate;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public double getGridArea() {
        return gridArea;
    }

    public void setGridArea(double gridArea) {
        this.gridArea = gridArea;
    }

    public double getUnqualifiedRate() {
        return unqualifiedRate;
    }

    public void setUnqualifiedRate(double unqualifiedRate) {
        this.unqualifiedRate = unqualifiedRate;
    }

    public double getRepeatedRate() {
        return repeatedRate;
    }

    public void setRepeatedRate(double repeatedRate) {
        this.repeatedRate = repeatedRate;
    }

    public double getUnitMileage() {
        return unitMileage;
    }

    public void setUnitMileage(double unitMileage) {
        this.unitMileage = unitMileage;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public double getCenterSpacing() {
        return centerSpacing;
    }

    public void setCenterSpacing(double centerSpacing) {
        this.centerSpacing = centerSpacing;
    }

    public double getGraphicDegree() {
        return graphicDegree;
    }

    public void setGraphicDegree(double graphicDegree) {
        this.graphicDegree = graphicDegree;
    }

    public double getLeakageRate() {
        return leakageRate;
    }

    public void setLeakageRate(double leakageRate) {
        this.leakageRate = leakageRate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }
}
