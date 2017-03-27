package model;

public class Index {
    private int daySerial;
    private int blockSerial;

    public Index(int daySerial, int blockSerial){
        this.daySerial = daySerial;
        this.blockSerial = blockSerial;
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
}
