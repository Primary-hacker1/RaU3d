package com.rick.recoveryapp.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class RecordDetailed {

    @Id(autoincrement = true)
    private Long id;
    private long RecordID;//记录编号
    private String ActivtType;//运动类型
    private String RecordTime;//记录时间
    private int Speed;//转速
    private int Resistance;//阻力
    private int LeftLimb;//左肢
    private int RightLimb;//右肢
    private int heartRate;//心率
    private int Hbo2;//血氧饱和度
    private int spasm;//痉挛等级
//    private int spasmCount;//痉挛次数

    @Generated(hash = 1299379076)
    public RecordDetailed(Long id, long RecordID, String ActivtType, String RecordTime, int Speed, int Resistance, int LeftLimb, int RightLimb, int heartRate, int Hbo2, int spasm) {
        this.id = id;
        this.RecordID = RecordID;
        this.ActivtType = ActivtType;
        this.RecordTime = RecordTime;
        this.Speed = Speed;
        this.Resistance = Resistance;
        this.LeftLimb = LeftLimb;
        this.RightLimb = RightLimb;
        this.heartRate = heartRate;
        this.Hbo2 = Hbo2;
        this.spasm = spasm;
    }

    @Generated(hash = 189556252)
    public RecordDetailed() {
    }

    public long getRecordID() {
        return RecordID;
    }

    public void setRecordID(long RecordID) {
        this.RecordID = RecordID;
    }

    public String getActivtType() {
        return ActivtType;
    }

    public void setActivtType(String activtType) {
        ActivtType = activtType;
    }

    public String getRecordTime() {
        return RecordTime;
    }

    public void setRecordTime(String recordTime) {
        RecordTime = recordTime;
    }

    public int getSpeed() {
        return Speed;
    }

    public void setSpeed(int speed) {
        Speed = speed;
    }

    public int getResistance() {
        return Resistance;
    }

    public void setResistance(int resistance) {
        Resistance = resistance;
    }

    public int getLeftLimb() {
        return LeftLimb;
    }

    public void setLeftLimb(int leftLimb) {
        LeftLimb = leftLimb;
    }

    public int getRightLimb() {
        return RightLimb;
    }

    public void setRightLimb(int rightLimb) {
        RightLimb = rightLimb;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }


    public int getHbo2() {
        return Hbo2;
    }

    public void setHbo2(int hbo2) {
        Hbo2 = hbo2;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSpasm() {
        return this.spasm;
    }

    public void setSpasm(int spasm) {
        this.spasm = spasm;
    }

//    public int getSpasmCount() {
//        return this.spasmCount;
//    }
//
//    public void setSpasmCount(int spasmCount) {
//        this.spasmCount = spasmCount;
//    }
}
