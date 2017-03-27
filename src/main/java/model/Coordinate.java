package model;

/**
 * 平面坐标
 */
public class Coordinate {
    private double x;
    private double y;
    private double column;
    private double row;
    private int landBlock;
    private double graphicX;
    private double graphicY;
    private int eageFlag;//标记坐标是否为边界点
    private int depth;
    private String wagonNum;
    private int serial;//索引地块
    private double edgeDistance;
    private int daySerial;//索引日期
    private int blockSerial;//索引日期下的地块号
    private int colourSerial;//标记不同地块颜色
    private double distance;
    private int blockIndex;

    public Coordinate() {}

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
        this.column = 1;
        this.row = 1;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getColumn() {
        return column;
    }

    public void setColumn(double column) {
        this.column = column;
    }

    public double getRow() {
        return row;
    }

    public void setRow(double row) {
        this.row = row;
    }

    public int getLandBlock() {
        return landBlock;
    }

    public void setLandBlock(int landBlock) {
        this.landBlock = landBlock;
    }

    public double getGraphicX() {
        return graphicX;
    }

    public void setGraphicX(double graphicX) {
        this.graphicX = graphicX;
    }

    public double getGraphicY() {
        return graphicY;
    }

    public void setGraphicY(double graphicY) {
        this.graphicY = graphicY;
    }

    public int getEageFlag() {
        return eageFlag;
    }

    public void setEageFlag(int eageFlag) {
        this.eageFlag = eageFlag;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getWagonNum() {
        return wagonNum;
    }

    public void setWagonNum(String wagonNum) {
        this.wagonNum = wagonNum;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public double getEdgeDistance() {
        return edgeDistance;
    }

    public void setEdgeDistance(double edgeDistance) {
        this.edgeDistance = edgeDistance;
    }

    public int getDaySerial() {
        return daySerial;
    }

    public void setDaySerial(int daySerial) {
        this.daySerial = daySerial;
    }

    public int getBlockSerial() {
        return blockSerial;
    }

    public void setBlockSerial(int blockSerial) {
        this.blockSerial = blockSerial;
    }

    public int getColourSerial() {
        return colourSerial;
    }

    public void setColourSerial(int colourSerial) {
        this.colourSerial = colourSerial;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }
}