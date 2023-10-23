package com.rick.recoveryapp.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class ActivitRecord {

    @Id
    private Long ID;//记录编号
    private String UserName;//患者名称
    private String UserNumber;//患者病历号
    private String ActivtType;//运动类型
    private String RecordTime;//记录时间
    private String LongTime;//训练时长
    private String Aduration;//训练时长
    private String Pduration;//训练时长
    private String total_mileage;//总里程
    private String Calories; //卡路里
    private String Remark;//备注
    private String B_Diastole_Shrink;//血压 前
    private String L_Diastole_Shrink;//血压 后
    private String spasmCount;//痉挛次数

    @Generated(hash = 1947348783)
    public ActivitRecord(Long ID, String UserName, String UserNumber, String ActivtType, String RecordTime, String LongTime,
            String Aduration, String Pduration, String total_mileage, String Calories, String Remark, String B_Diastole_Shrink,
            String L_Diastole_Shrink, String spasmCount) {
        this.ID = ID;
        this.UserName = UserName;
        this.UserNumber = UserNumber;
        this.ActivtType = ActivtType;
        this.RecordTime = RecordTime;
        this.LongTime = LongTime;
        this.Aduration = Aduration;
        this.Pduration = Pduration;
        this.total_mileage = total_mileage;
        this.Calories = Calories;
        this.Remark = Remark;
        this.B_Diastole_Shrink = B_Diastole_Shrink;
        this.L_Diastole_Shrink = L_Diastole_Shrink;
        this.spasmCount = spasmCount;
    }
    public String getAduration() {
        return Aduration;
    }

    public void setAduration(String aduration) {
        Aduration = aduration;
    }

    public String getPduration() {
        return Pduration;
    }

    public void setPduration(String pduration) {
        Pduration = pduration;
    }
    public String getActivtType() {
        return ActivtType;
    }

    public void setActivtType(String activtType) {
        ActivtType = activtType;
    }

    public String getTotal_mileage() {
        return total_mileage;
    }

    public void setTotal_mileage(String total_mileage) {
        this.total_mileage = total_mileage;
    }

    public String getCalories() {
        return Calories;
    }

    public void setCalories(String calories) {
        Calories = calories;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getB_Diastole_Shrink() {
        return B_Diastole_Shrink;
    }

    public void setB_Diastole_Shrink(String b_Diastole_Shrink) {
        B_Diastole_Shrink = b_Diastole_Shrink;
    }

    public String getL_Diastole_Shrink() {
        return L_Diastole_Shrink;
    }

    public void setL_Diastole_Shrink(String l_Diastole_Shrink) {
        L_Diastole_Shrink = l_Diastole_Shrink;
    }


    public String getLongTime() {
        return LongTime;
    }

    public void setLongTime(String longTime) {
        LongTime = longTime;
    }


    @Generated(hash = 626300031)
    public ActivitRecord() {
    }

    public Long getID() {
        return ID;
    }

    public void setRecordID(Long recordID) {
        ID = recordID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }


    public String getRecordTime() {
        return RecordTime;
    }

    public void setRecordTime(String recordTime) {
        RecordTime = recordTime;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getSpasmCount() {
        return this.spasmCount;
    }

    public void setSpasmCount(String spasmCount) {
        this.spasmCount = spasmCount;
    }

}
