package model;

/**
 * Created by Newsoul on 2017/2/21.
 */
public class SerialPoint extends cn.edu.hit.gpcs.commons.models.Point {
    private int serial;//颜色序号
    private int type;//绘制类型
    /**
     * @param x 横坐标
     * @param y 纵坐标
     */
    public SerialPoint(double x, double y) {
        super(x, y);
    }

    public int getGraphicSerial() {
        return serial;
    }

    public void setGraphicSerial(int serial) {
        this.serial = serial;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
