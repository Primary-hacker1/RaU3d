package com.rick.recoveryapp.utils.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created : Yx
 * Time : 2018/11/6 13:18
 * Describe :心跳- 心电图
 */
public class EcgView extends View {

    //表格画笔
    protected Paint mPaint;
    //心跳画笔
    protected Paint mPath;

    //网格颜色
    protected int mGridColor = Color.parseColor("#EDA5B5");
    //小网格颜色
    protected int mSGridColor = Color.parseColor("#EFE4F1");
    //背景颜色
    protected int mBackgroundColor = Color.parseColor("#F0EAF2");
    //心跳颜色
    protected int mEcgColor = Color.parseColor("#281520");
    //屏幕宽高px自身的大小
    protected int mWidth, mHeight;

    //网格宽度
    protected int mGridWidth = 50;
    //小网格的宽度
    protected int mSGridWidth = 10;
    private Context context;
    //屏幕宽度dp
    private int dpWidth;
    //屏幕高度px
    private int dpHeight;
    //屏幕密度
    private float scale;

    //数据
    private ArrayList<Integer> data1 = new ArrayList<>();
    private ArrayList<Integer> data2 = new ArrayList<>();
    private ArrayList<Integer> data3 = new ArrayList<>();
    //心的数据坐标
    private ArrayList<HashMap<Integer, Integer>> xlist = new ArrayList<>();

    public EcgView(Context context) {
        super(context);
        this.context = context;
        inItDisplayMetrics();
    }

    public EcgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inItDisplayMetrics();
    }

    public EcgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inItDisplayMetrics();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EcgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        inItDisplayMetrics();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//视图大小变化时调用
        mWidth = w;
        mHeight = h;
        Love();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 获取屏幕宽高，转化为DP
     */
    private void inItDisplayMetrics() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        int pxWidth = metric.widthPixels; // 屏幕宽度（像素）
        int pxHeight = metric.heightPixels;//屏幕高度（像素）
        scale = getResources().getDisplayMetrics().density;//屏幕密度
        //根据手机的分辨率从 px(像素) 的单位 转成为 dp
        dpWidth = (int) (pxWidth / scale + 0.5f);
        dpHeight = (int) (pxHeight / scale + 0.5f);
    }

    /**
     * 静态更新
     * 方法重载
     *
     * @param num       第几条心电图可以有1 2 3 4...n条
     * @param dataList  所有数据集合
     * @param ecgHeight 心电图高度
     */
    public void setDatas(int num, ArrayList<Integer> dataList, int ecgHeight) {
        int ecgIndex;
        switch (num) {
            case 1:
                ecgIndex = (int) (ecgHeight / 3 / 2); //第一条心电图基准位置
                for (int i = 0; i < dataList.size(); i++) {
                    data1.add(ecgIndex - dataList.get(i));
                }
                break;
            case 2:
                ecgIndex = (int) (ecgHeight / 2);//第二条心电图基准位置
                for (int i = 0; i < dataList.size(); i++) {
                    data2.add(ecgIndex - dataList.get(i));
                }
                break;
            case 3:
                ecgIndex = (int) (ecgHeight - (ecgHeight / 3 / 2));//第三条心电图基准位置
                for (int i = 0; i < dataList.size(); i++) {
                    data3.add(ecgIndex - dataList.get(i));
                }
                break;
        }
        postInvalidate();//界面刷新

    }

    /**
     * 实时更新
     *
     * @param number    动态传来的数据
     * @param ecgHeight 心电图高度
     */
    public void setDatas(int number, int ecgHeight) {
        //动态心电图基准位置
        int ecgIndex = (int) (ecgHeight - (ecgHeight / 3 / 2));
        data3.add(ecgIndex - number);
        postInvalidate();//界面刷新
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画网格
        initBackground(canvas);
        //画心跳
        mPath = new Paint();
        mPath.setColor(Color.RED);//画笔颜色
        mPath.setStrokeWidth(2.0F);
        initEcgOne(canvas);
        initEcgTwo(canvas);
        initEcgThree(canvas);
    }

    /**
     * 画网格
     *
     * @param canvas
     */
    private void initBackground(Canvas canvas) {
        mPaint = new Paint();
        canvas.drawColor(mBackgroundColor);
        //画小网格

        //竖线个数
        int vSNum = mWidth / mSGridWidth;

        //横线个数
        int hSNum = mHeight / mSGridWidth;
        mPaint.setColor(mSGridColor);
        mPaint.setStrokeWidth(2);
        //画竖线
        for (int i = 0; i < vSNum + 1; i++) {
            canvas.drawLine(i * mSGridWidth, 0, i * mSGridWidth, mHeight, mPaint);
        }
        //画横线
        for (int i = 0; i < hSNum + 1; i++) {

            canvas.drawLine(0, i * mSGridWidth, mWidth, i * mSGridWidth, mPaint);
        }

        //竖线个数
        int vNum = mWidth / mGridWidth;
        //横线个数
        int hNum = mHeight / mGridWidth;
        mPaint.setColor(mGridColor);
        mPaint.setStrokeWidth(2);
        //画竖线
        for (int i = 0; i < vNum + 1; i++) {
            canvas.drawLine(i * mGridWidth, 0, i * mGridWidth, mHeight, mPaint);
        }
        //画横线
        for (int i = 0; i < hNum + 1; i++) {
            canvas.drawLine(0, i * mGridWidth, mWidth, i * mGridWidth, mPaint);
        }

    }

    /**
     * 画心跳1
     *
     * @param canvas
     */
    private void initEcgOne(Canvas canvas) {
        if (data1.size() > 0) {
            //    画搏动折线
            for (int k = 1; k < data1.size(); k++) {
                canvas.drawLine((k - 1) * 5 * scale, data1.get(k - 1) * scale, k * 5 * scale, data1.get(k) * scale, mPath);
            }
        }
    }

    /**
     * 画心跳2
     *
     * @param canvas
     */
    private void initEcgTwo(Canvas canvas) {
        if (data2.size() > 0) {
            //    画搏动折线
            for (int k = 1; k < data2.size(); k++) {
                canvas.drawLine((k - 1) * 5 * scale, data2.get(k - 1) * scale, k * 5 * scale, data2.get(k) * scale, mPath);
            }
        }
    }

    /**
     * 画心跳3
     *
     * @param canvas
     */
    private void initEcgThree(Canvas canvas) {
        if (data3.size() > 0) {
            int ecgIndex = (int) (300 - (300 / 3 / 2));
            for (int k = 1; k < data3.size(); k++) {

                if (data3.get(k) == ecgIndex) {//当数据等于基准线时就画心
                    //画心
                    float b = (k) * 5 * scale;
                    float x = 0;
                    for (int i = 1; i < xlist.size(); i++) {
                        HashMap<Integer, Integer> map1 = xlist.get(i - 1);
                        int x1 = map1.get(0);
                        int y1 = map1.get(1);
                        HashMap<Integer, Integer> map2 = xlist.get(i);
                        int x2 = map2.get(0);
                        int y2 = map2.get(1);
                        if (i == 1) {
                            x = x1 - b;
                        }
                        canvas.drawLine(x1 - x, y1, x2 - x, y2, mPath);
                    }
                } else {
                    //画心率
                    canvas.drawLine((k - 1) * 5 * scale, data3.get(k - 1) * scale, k * 5 * scale, data3.get(k) * scale, mPath);
                }

            }
        }
        //一屏可绘制点数
        int d = mWidth / (int) (5 * scale);//每(5 * scales)dp一次一点，屏幕宽度/它等于一屏能显示的点数
        //动起来
        if (data3.size() > d) {
            data3.remove(0);//移除左边图形  让图形移动起来
        }
    }

    /**
     * 生成心的数据
     */
    private void Love() {
        xlist.clear();
        int h = (int) (300 - (300 / 3 / 2));
        final float scale = context.getResources().getDisplayMetrics().density * 2;//调整心 大小
        for (int i = 0 + 180; i < 361 + 180; i = i + 10) {
            double sit = i * 2 * Math.PI / 360d;
            double x = scale * 10 * Math.pow(Math.sin(sit), 3);//偏移量
            double y = scale * (7 * Math.cos(sit) - 5 * Math.cos(2 * sit) - 2 * Math.cos(3 * sit) - Math.cos(4 * sit));
            HashMap<Integer, Integer> map = new HashMap();
            map.put(0, (int) ((mWidth - mWidth / 3 / 2) + x));
            map.put(1, (int) ((mHeight - mHeight / 3 / 2) - y - (scale * 10)));//调整心 高度未知
            xlist.add(map);

        }
    }


}
