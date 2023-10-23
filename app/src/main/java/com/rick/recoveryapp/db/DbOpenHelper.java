package com.rick.recoveryapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Created by Administrator on 2017/4/5.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbOpenHelper";
    public static final String DB_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.xui.recovery/data.db";
    //  /storage/emulated/0/com.xui/data.db
    // public static final String DB_NAME ="data.db";
    //--/storage/sdcard0/
    private static DbOpenHelper db;

    public DbOpenHelper(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/xhttpapp/data.db", null, 1);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (db == null) {
            db = new DbOpenHelper(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "";
        //region 表结构

        sql = "create table tb_EcgData(" +
                "ID     varchar(30)," +
                "CoorY nvarchar(80)" +
                ")";
        db.execSQL(sql);

        //上架库位表
        sql = "create table bs_wait_onshelf(" +
                "materielBarcode  varchar(30)," +   //物料条码
                "rackBarcode  varchar(30)," +       //料架条码
                "scanTime   varchar(30)" +          //上架时间
                ")";
        db.execSQL(sql);

        //异常处理表
        sql = "create table Exception_handling(" +
                "ManagementID     varchar(30)," +        //处置表ID
                "ManagementName   varchar(30)" +  //处置名称
                ")";
        db.execSQL(sql);


        //原因
        sql = "create table PubBasicsDetailReasons(" +
                "BasicsDetailRID     varchar(30)," +         //明细表ID
                "RMainID     varchar(30)," +         //关联主表基础信息ID
                "BasicsDetailRName   varchar(30)" +           //原因名称
                ")";
        db.execSQL(sql);

        //现象
        sql = "create table PubUnqualifieds(" +
                "DEPNAME  varchar(30),  " +            //名称
                "CLASSLEVEL   varchar(30)," +         //第几级别
                "DEPPID   varchar(30)," +              //上层ID
                "DEPID   varchar(30)," +               //自身表ID
                "DEPCD   varchar(30)" +             //关联主表基础信息ID
                ")";
        db.execSQL(sql);

        sql = "create table ServiceData(" +
                "Barcode                varchar(30)," +
                "DetailID               varchar(30)," +
                "SectionID              varchar(30)," +
                "BasicsMainID           varchar(30)," +     //第一级
                "BasicsDetailPID        varchar(30)," +     //最终级
                "BasicsDetailRID        varchar(30)," +     //原因信息ID
                "ManagementID           varchar(30)," +      //处理方式ID
                "RepairStatic           varchar(30)," +      //状态  改变颜色
                "RepairID               varchar(30)," +       //维修人工段号
                "CodeIdStatic           varchar(30)," +        // 单据状态   n Y	(上传成功为Y，失败未n)
                "FristFault             varchar(30)," +
                "BasicsDetailRID1       varchar(30)," +
                "BasicsDetailRID2       varchar(30)," +
                "FourthFault            varchar(30)," +             //关联主表基础信息ID
                "ReasonName             varchar(30)," +           //原因名称
                "ApproachName           varchar(30)" +           //处理方法名称
                ")";
//维修ID

        db.execSQL(sql);

        //    故障现象表
//        sql=" create table  Fristfault("+
//            "FristPhenomenon    varchar(30),"+	//-- 故障现象类型(第一级故障现象)
//            "FristfaultCode	varchar(30),"+	//--     故障现象ID
//            "PhenomenonId       varchar(30)"+	//-- 故障现象类型层级(第一级故障现象)
//                ")";
//        db.execSQL(sql);

        //  --该现象层级1234
        // 上一层ID


//        //更新时间表
//        sql="create table BatchDateQuery(" +
//                "tpubparttypeUpdateDate varchar(30)," +
//                "tpubpartUpdateDate varchar(30)," +
//                "tpubpartFaultUpdateDate varchar(30)," +
//                "tpubfaulttypeUpdateDate varchar(30)," +
//                "tpubfaultUpdateDate varchar(30)," +
//                "tpubunqreasonUpdateDate varchar(30)," +
//                "tpubdepartmentUpdateDate varchar(30)," +
//                "tpubcheckuserUpdateDate varchar(30)"+
//                ")";
//        db.execSQL(sql);

        //检验、维修人员表
//        sql="create table TPUBCheckUser(" +
//                "id varchar(10)," +
//                "userid varchar(20),"+
//                "UserName varchar(20),"+
//                "Password varchar(50),"+
//                "DepartmentCode varchar(20),"+
//                "UpdatedTime varchar(20),"+
//                "CheckType varchar(10),"+
//                "CheckRepair varchar(1),"+
//                "IfValid varchar(1),"+
//                "Status varchar(1)"+
//                ")";
//        db.execSQL(sql);
//        //责任部门表
//        sql="create table Tpubdepartment(" +
//                "code varchar(10)," +
//                "cname varchar(50),"+
//                "factory varchar(10),"+
//                "UpdatedTime varchar(20),"+
//                "IfValid varchar(1),"+
//                "Status varchar(1)"+
//                ")";
//        db.execSQL(sql);
//
//        //部品分类表
//        sql="create table Tpubparttype(" +
//                "typecode varchar(10)," +
//                "typename varchar(50),"+
//                "helpstr varchar(50),"+
//                "UpdatedTime varchar(20),"+
//                "typepicture TEXT,"+
//                "fitmt varchar(1),"+
//                "fitem varchar(1),"+
//                "IfValid varchar(1),"+
//                "Status varchar(1)"+
//                ")";
//        db.execSQL(sql);
//
//        //部品表
//        sql="create table Tpubpart(" +
//                "partcode varchar(10)," +
//                "partname varchar(50),"+
//                "typecode varchar(10),"+
//                "describe varchar(50)," +
//                "helpstr varchar(50),"+
//                "UpdatedTime varchar(20),"+
//                "partpicture TEXT,"+
//                "fitmt varchar(1),"+
//                "fitem varchar(1),"+
//                "IfValid varchar(1),"+
//                "Status varchar(1)"+
//                ")";
//        db.execSQL(sql);
//
//        //部品故障对应表
//        sql="create table Tpubpartfault(" +
//                "PartCode varchar(10)," +
//                "PartName varchar(50),"+
//                "FaultCode varchar(10),"+
//                "FaultName varchar(50)," +
//                "faultTypeCode varchar(10),"+
//                "faultTypeName varchar(50),"+
//                "UpdatedTime varchar(20),"+
//                "IfValid varchar(1)"+
//                ")";
//        db.execSQL(sql);
//
//        //故障分类表
//        sql="create table Tpubfaulttype(" +
//                "typecode varchar(10)," +
//                "typename varchar(50),"+
//                "helpstr varchar(10),"+
//                "UpdatedTime varchar(20),"+
//                "typepic TEXT,"+
//                "IfValid varchar(1),"+
//                "Status varchar(1)"+
//                ")";
//        db.execSQL(sql);
//
//        //故障现象表
//        sql="create table Tpubfault(" +
//                "faultcode varchar(10)," +
//                "faultname varchar(50),"+
//                "faultdescribe varchar(50)," +
//                "UpdatedTime varchar(20),"+
//                "helpstr varchar(50),"+
//                "faultpic TEXT,"+
//                "IfValid varchar(1),"+
//                "Status varchar(1)"+
//                ")";
//        db.execSQL(sql);
//
//        //不良原因表
//        sql="create table Tpubunqreason(" +
//                "unqualifiedcode varchar(10)," +
//                "unqualifiedname varchar(50),"+
//                "UpdatedTime varchar(20),"+
//                "IfValid varchar(1),"+
//                "Status varchar(1)"+
//                ")";
//        db.execSQL(sql);
//
//        //初检单主表
//        sql="create table Tchkbillmain(" +
//                "appid varchar(30)," +
//                "frameNo varchar(20),"+
//                "modelCode varchar(50),"+
//                "modelColorCode varchar(20),"+
//                "opeTime varchar(20),"+
//                "createdate varchar(20),"+
//                "createman varchar(20),"+
//                "checktime varchar(20),"+
//                "checkman varchar(20),"+
//                "checktype varchar(20),"+
//                "status varchar(1),"+
//                "UploadStatus varchar(1)"+
//                ")";
//        db.execSQL(sql);
//        //初检单明细表
//        sql="create table Tchkbilldetail(" +
//                "id varchar(50)," +
//                "appid varchar(30)," +
//                "appmid varchar(20),"+
//                "partcode varchar(50),"+
//                "partname varchar(20),"+
//                "parttypecode varchar(20),"+
//                "faultcode varchar(20),"+
//                "faultname varchar(20),"+
//                "faulttypecode varchar(20),"+
//                "unqualifiedcode1 varchar(20),"+
//                "dutydeptcode1 varchar(20),"+
//                "judgetime1 varchar(20),"+
//                "judgeman1 varchar(20),"+
//                "unqualifiedcode2 varchar(20),"+
//                "dutydeptcode2 varchar(20),"+
//                "judgetime2 varchar(20),"+
//                "judgeman2 varchar(20),"+
//                "judgestatus varchar(20),"+
//                "checkstatus varchar(20),"+
//                "repairstatus varchar(20)"+
//                ")";
//        db.execSQL(sql);
//
//        //检验子表
//        sql="create table Tchkbillcheckchild(" +
//                "id varchar(50)," +
//                "did varchar(50)," +
//                "appid varchar(30)," +
//                "appmid varchar(20),"+
//                "appdid varchar(50),"+
//                "checkman varchar(20),"+
//                "checktime varchar(20),"+
//                "checkstatus varchar(20)"+
//                ")";
//        db.execSQL(sql);
//
//        //维修子表
//        sql="create table Tchkbillrepairchild(" +
//                "id varchar(50)," +
//                "appid varchar(50)," +
//                "appmid varchar(20),"+
//                "appdid varchar(50),"+
//                "repairman varchar(20),"+
//                "repairtime varchar(20),"+
//                "repairtype varchar(20)"+
//                ")";
//        db.execSQL(sql);
//
//        //生成流水号表
//        sql="create table tpubLiuShui(" +
//                "name varchar(30)," +
//                "id varchar(20),"+
//                "no int"+
//                ")";
//        db.execSQL(sql);
//
//        sql="insert into tpubLiuShui(name,id,no) values('Tchkbillmain','20180101',1)";
//        db.execSQL(sql);
//        sql="insert into tpubLiuShui(name,id,no) values('Tchkbilldetail','20180101',1)";
//        db.execSQL(sql);
//        sql="insert into tpubLiuShui(name,id,no) values('Tchkbillcheckchild','20180101',1)";
//        db.execSQL(sql);
//        sql="insert into tpubLiuShui(name,id,no) values('Tchkbillrepairchild','20180101',1)";
//        db.execSQL(sql);
//        sql="insert into tpubLiuShui(name,id,no) values('fujian','20180101',1)";
//        db.execSQL(sql);
        //endregion
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE tb_EcgData");
    }
}
