package com.rick.recoveryapp.greendao.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MacDr {

    @Id
    private Long ID;//记录编号
    private String BlueThMac;//蓝牙地址
    private String EcgMac;//心电地址
    private String BloodMac; //血压地址
    private String OxygenMac;//血氧地址

    @Generated(hash = 1390067254)
    public MacDr(Long ID, String BlueThMac, String EcgMac, String BloodMac,
            String OxygenMac) {
        this.ID = ID;
        this.BlueThMac = BlueThMac;
        this.EcgMac = EcgMac;
        this.BloodMac = BloodMac;
        this.OxygenMac = OxygenMac;
    }
    @Generated(hash = 1724995817)
    public MacDr() {
    }
    public Long getID() {
        return this.ID;
    }
    public void setID(Long ID) {
        this.ID = ID;
    }
    public String getBlueThMac() {
        return this.BlueThMac;
    }
    public void setBlueThMac(String BlueThMac) {
        this.BlueThMac = BlueThMac;
    }
    public String getEcgMac() {
        return this.EcgMac;
    }
    public void setEcgMac(String EcgMac) {
        this.EcgMac = EcgMac;
    }
    public String getBloodMac() {
        return this.BloodMac;
    }
    public void setBloodMac(String BloodMac) {
        this.BloodMac = BloodMac;
    }
    public String getOxygenMac() {
        return this.OxygenMac;
    }
    public void setOxygenMac(String OxygenMac) {
        this.OxygenMac = OxygenMac;
    }



}
