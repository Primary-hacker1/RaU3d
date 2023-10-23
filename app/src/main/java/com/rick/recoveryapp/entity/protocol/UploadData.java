package com.rick.recoveryapp.entity.protocol;

public class UploadData {

    private String oxy_vaulestr;
    private String ActiveType;
    private String ActiveState;
    private int SpasmState;
    private String time;
    private String speed;
    private String left;
    private String right;
    private String ECG;
    private String blood;
    private String blood_oxy;
    private String high;
    private String low;

    private String STresistance;
    private String STspasm;
    private String STspeed;
    private String STtime;

    public String getSTresistance() {
        return STresistance;
    }

    public void setSTresistance(String STresistance) {
        this.STresistance = STresistance;
    }

    public String getSTspasm() {
        return STspasm;
    }

    public void setSTspasm(String STspasm) {
        this.STspasm = STspasm;
    }

    public String getSTspeed() {
        return STspeed;
    }

    public void setSTspeed(String STspeed) {
        this.STspeed = STspeed;
    }

    public String getSTtime() {
        return STtime;
    }

    public void setSTtime(String STtime) {
        this.STtime = STtime;
    }



    public int getSpasmState() {
        return SpasmState;
    }

    public void setSpasmState(int spasmState) {
        SpasmState = spasmState;
    }


//   private String oxy_vaule ;

    public String getOxy_vaulestr() {
        return oxy_vaulestr;
    }

    public void setOxy_vaulestr(String oxy_vaulestr) {
        this.oxy_vaulestr = oxy_vaulestr;
    }

    public String getActiveType() {
        return ActiveType;
    }

    public void setActiveType(String activeType) {
        ActiveType = activeType;
    }

    public String getActiveState() {
        return ActiveState;
    }

    public void setActiveState(String activeState) {
        ActiveState = activeState;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getECG() {
        return ECG;
    }

    public void setECG(String ECG) {
        this.ECG = ECG;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getBlood_oxy() {
        return blood_oxy;
    }

    public void setBlood_oxy(String blood_oxy) {
        this.blood_oxy = blood_oxy;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

//    public String getOxy_vaule() {
//        return oxy_vaule;
//    }
//
//    public void setOxy_vaule(String oxy_vaule) {
//        this.oxy_vaule = oxy_vaule;
//    }
}
