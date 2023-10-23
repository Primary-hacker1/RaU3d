package com.rick.recoveryapp.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EcgDataDB {

    /**
     * @id 设置是键id Long 类型，可以通过@Id(autoincrement = true)设置自动增长（自动增长主键不能用基本类型long，只能用包装类型Long）
     * @Index(unique = true) 是向数据库添加了唯一约束。
     */
    @Id(autoincrement = true)
    private Long db_id;

    private Float cooY;

    private String RecordID;//记录编号

    @Generated(hash = 1698558240)
    public EcgDataDB(Long db_id, Float cooY, String RecordID) {
        this.db_id = db_id;
        this.cooY = cooY;
        this.RecordID = RecordID;
    }

    @Generated(hash = 672404028)
    public EcgDataDB() {
    }

    public Long getDb_id() {
        return db_id;
    }

    public void setDb_id(Long db_id) {
        this.db_id = db_id;
    }

    public Float getCooY() {
        return cooY;
    }

    public void setCooY(Float cooY) {
        this.cooY = cooY;
    }

    public String getRecordID() {
        return this.RecordID;
    }

    public void setRecordID(String RecordID) {
        this.RecordID = RecordID;
    }
}
