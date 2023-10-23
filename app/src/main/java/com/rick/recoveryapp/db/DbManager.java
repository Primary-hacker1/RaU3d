package com.rick.recoveryapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xuexiang.xutil.data.DateUtils;

/**
 * Created by Administrator on 2017/4/5.
 */

public  class  DbManager  {

    private static final String TAG = "DbManager";
    public static SQLiteDatabase db;
    private static DbOpenHelper helper;
    private static Context context;
    private static String tableOrderName="tb_order";
    private static String tableUserName="PDA_USER";


    public  DbManager(Context context)
    {
       this.context=context;
    }
    //打开数据库
    public void open()
    {
        helper= DbOpenHelper.getInstance(context);
        db = helper.getWritableDatabase();
    }

    public void exeSqlA(String sql, Object[] bindArgs) {
        db.execSQL(sql, bindArgs);
    }

    public void exeSqlA(String sql) {
        db.execSQL(sql);
    }

//返回游标
    public Cursor exeSql(String sql) {
        //db.execSQL(sql);
        return db.rawQuery(sql, null);
    }

    public static long insertOrder(String name, String skuCode, String barCode, String standardName, String orderNum, String scanTime)
    {
        ContentValues values=new ContentValues();
        values.put("skuName",name);
        values.put("skuCode",skuCode);
        values.put("barCode",barCode);
        values.put("standardName",standardName);
        values.put("orderNum",orderNum);
        values.put("scanTime",scanTime);
        return db.insert(tableOrderName,null,values);
    }

    public static long insertShipConfirm(String ps_no, String prd_no, String bat_no, String qty, String name)
    {
        ContentValues values=new ContentValues();
        values.put("ps_no",ps_no);
        values.put("prd_no",prd_no);
        values.put("bat_no",bat_no);
        values.put("qty",qty);
        values.put("ScanTime","");
        values.put("UserID","");
        values.put("ysqty",0);
        values.put("state","N");
        values.put("prd_name",name);
        return db.insert(tableOrderName,null,values);
    }

    public static Cursor query(String barcode, String opertype, String fileName)
    {
        try {
            String[] columns=new String[]{"count(*)"};
            String sqlWhere="opertype=? and barcode=? and txtfilename=?";
            String[] selectArgs=new String[]{opertype,barcode,fileName};
            Cursor cursor=db.query(tableOrderName,columns,sqlWhere,selectArgs,null,null,null);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }
    public static Cursor query(String opertype)
    {
        try {
            String[] columns=new String[]{"*"};
            String sqlWhere="opertype=?";
            String[] selectArgs=new String[]{opertype};
            Cursor cursor=db.query(tableOrderName,columns,sqlWhere,selectArgs,null,null,null);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }
    public static Cursor queryShipConfirm()
    {
        try {
            String[] columns=new String[]{"*"};
            String sqlWhere="state='Y'";
            //String [] selectArgs=new String []{ps_no};
            Cursor cursor=db.query(tableOrderName,columns,sqlWhere,null,null,null,null);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }

    public static Cursor queryShipConfirm(String barCode)
    {
        try {
            String[] columns=new String[]{"*"};
            String sqlWhere="ps_no=?";
            String[] selectArgs=new String[]{barCode};
            Cursor cursor=db.query(tableOrderName,columns,sqlWhere,selectArgs,null,null,null);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }

    public static Cursor queryQty(String ps_no, String bat_no)
    {
        try {
            String[] columns=new String[]{"*"};//qty,ysqty
            String sqlWhere="ps_no=? and bat_no=?";
            String[] selectArgs=new String[]{ps_no,bat_no};
            Cursor cursor=db.query(tableOrderName,columns,sqlWhere,selectArgs,null,null,null);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }


    public static Cursor queryAll()
    {
        try {
           // String [] columns=new String []{"*"};
            String sql="select distinct ps_no from PDA_ShipConfirm";
           // String [] selectArgs=new String []{,,};
            Cursor cursor=db.rawQuery(sql,null);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }
    public int  queryShipConfirmState(String ps_no)
    {
        try {
            String[] columns=new String[]{"*"};
            String sqlWhere="ps_no=? and state='N'";
            String[] selectArgs=new String[]{ps_no};
            Cursor cursor=db.query(tableOrderName,columns,sqlWhere,selectArgs,null,null,null);
            if(cursor.getCount()==0)
            {
                return 0;
            }else
            {
                return 1;
            }

        } catch (Exception e) {
            return -1;
        }
    }

    public int  queryShipConfirmHangState(String ps_no, String prd_no)
    {
        try {
            String[] columns=new String[]{"*"};
            String sqlWhere="ps_no=? and prd_no=? and state='N'";
            String[] selectArgs=new String[]{ps_no,prd_no};
            Cursor cursor=db.query(tableOrderName,columns,sqlWhere,selectArgs,null,null,null);
            if(cursor.getCount()==0)
            {
                return 0;
            }else
            {
                return 1;
            }

        } catch (Exception e) {
            return -1;
        }
    }

    public static Cursor queryUser(String UserID, String UserPwd)
    {
        try {
            String[] columns=new String[]{"*"};
            String sqlWhere="UserID=? and UserPwd=?";
            String[] selectArgs=new String[]{UserID,UserPwd};
            Cursor cursor=db.query(tableUserName,columns,sqlWhere,selectArgs,null,null,null);
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }


    public static void insertShipConfirm(String ps_no, String prd_no, String bat_no, String qty, String ScanTime, String UserID)
    {
        ContentValues values=new ContentValues();
        values.put("ps_no",ps_no);
        values.put("prd_no",prd_no);
        values.put("bat_no",bat_no);
        values.put("qty",qty);
        values.put("ScanTime",ScanTime);
        values.put("UserID",UserID);

        db.insert(tableOrderName,null,values);
    }

    public static void insertUser(String UserID, String UserName, String UserPwd, String UserType)
    {
        ContentValues values=new ContentValues();
        values.put("UserID",UserID);
        values.put("UserName",UserName);
        values.put("UserPwd",UserPwd);
        values.put("UserType",UserType);

        db.insert(tableUserName,null,values);
    }

    public static void update(String barcode, String opertype, String fileName, String nowtime)
    {
        try
        {
            ContentValues values=new ContentValues();
            values.put("opertime",nowtime);
            String sqlWhere="opertype=? and barcode=? and txtfilename=?";
            String[] selectArgs={opertype,barcode,fileName};
            db.update(tableOrderName,values,sqlWhere,selectArgs);
        }
        catch (Exception ex)
        {
            return;
        }
    }

    //关闭数据库
    public static void closeDB() {
        db.close();
    }
}
