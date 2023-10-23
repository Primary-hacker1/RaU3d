package com.rick.recoveryapp.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PowerAVG {

    @Id(autoincrement = true)
    private Long id;
    private String RecordID;//记录编号
    private String LeftAvg;
    private String RightAvg;


    @Generated(hash = 409052306)
    public PowerAVG(Long id, String RecordID, String LeftAvg, String RightAvg) {
        this.id = id;
        this.RecordID = RecordID;
        this.LeftAvg = LeftAvg;
        this.RightAvg = RightAvg;
    }
    @Generated(hash = 1318503102)
    public PowerAVG() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRecordID() {
        return this.RecordID;
    }
    public void setRecordID(String RecordID) {
        this.RecordID = RecordID;
    }
    public String getLeftAvg() {
        return this.LeftAvg;
    }
    public void setLeftAvg(String LeftAvg) {
        this.LeftAvg = LeftAvg;
    }
    public String getRightAvg() {
        return this.RightAvg;
    }
    public void setRightAvg(String RightAvg) {
        this.RightAvg = RightAvg;
    }


}
