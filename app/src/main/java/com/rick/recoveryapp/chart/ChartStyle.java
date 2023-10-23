package com.rick.recoveryapp.chart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.rick.recoveryapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChartStyle {

    LineChart chart;

    public ChartStyle(LineChart chart) {
        this.chart = chart;
    }

    public void initChartStyle() {

        chart.setDrawGridBackground(false);//设置网格背景
        chart.getDescription().setEnabled(false);//文字描述
        chart.setDrawBorders(false);//设置边框
        //禁用Y轴左侧
        chart.getAxisLeft().setEnabled(true);
        //禁用Y轴右侧轴线
        chart.getAxisRight().setDrawAxisLine(false);
        //不画网格线
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);

        // 开启手势触摸
        chart.setTouchEnabled(true);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        //设置高光每拖动启用
        chart.setHighlightPerDragEnabled(true);
        //设置比例启用
        chart.setScaleEnabled(true);
        //设置背景
        //  chart.setBackgroundColor(R.color.xui_btn_disable_color);
        //动画
        chart.animateX(1000);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        /**
         * 初始化湿度图表的 标题 样式
         */
        initChartLabel();
        initChartXAxis();
    }

    protected void initChartLabel() {

        /**
         * 初始化温度图表的 标题 样式
         */
        Legend l = chart.getLegend();
        l.setDrawInside(false);
       // l.setForm(Legend.LegendForm.SQUARE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
      //  l.setForm(Legend.LegendForm.LINE);
        l.setMaxSizePercent(15f);
        l.setTextSize(12f);
        l.setForm(Legend.LegendForm.CIRCLE); //设置成圆形图例
//        l.setTextSize(LegendTextSize);
//        l.setTextColor (legendColor);


    }

    protected void initChartXAxis() {
        /**
         * 初始化温度图表的 X,Y轴 样式
         */
        XAxis xAxis = chart.getXAxis();
        // xAxis.setTypeface(tfLight);
       // xAxis.setTextSize(10f);
        xAxis.setTextColor(ColorTemplate.getHoloBlue());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        //  xAxis.setDrawAxisLine(true);//是否绘制轴线
        //  xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
     //   xAxis.setAxisMaximum(9f);
        xAxis.setAxisMinimum(0f);//设置x轴最小值，不设置最大值自动增长
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisLineColor(Color.BLACK);
      //  xAxis.sett

        YAxis leftAxis = chart.getAxisLeft();
       // leftAxis.setTextSize(10f);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
//        leftAxis.setAxisMaximum(60f);
//        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setAxisLineColor(Color.BLACK);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);   //设置是否使用 Y轴右边的

    }

//    public void setChartData(int count, float range) {
////        List<ILineDataSet> dataSets = new ArrayList<>();
////        List<ILineDataSet> dataSets1 = new ArrayList<>();
//        LineDataSet d = null, d1=null;
//        for (int z = 0; z < 2; z++) {
//            List<Entry> values = new ArrayList<>();
//            List<Entry> values1 = new ArrayList<>();
//            //设置数据源
//            for (int i = 0; i < count; i++) {
//                double val = (Math.random() * range) + 3;
//                double val1 = (Math.random() * range) + 1.26;
//                values.add(new Entry(i, (float) val));
//                values1.add(new Entry(i,(float) val1));
//            }
//
//            d = new LineDataSet(values, "左肢" );
//            d.setLineWidth(2.5f);
//            d.setCircleRadius(4f);
//            d.setColor(ColorTemplate.getHoloBlue());
//            d.setCircleColor(Color.GRAY);//圆圈颜色
//        //    dataSets.add(d);
//
//            d1 = new LineDataSet(values1, "右肢");
//            d1.setLineWidth(2.5f);
//            d1.setCircleRadius(4f);
//            d1.setColor(Color.RED);
//            d1.setCircleColor(Color.GRAY);
//          //  dataSets1.add(d1);
//         //   int color = colors[z % colors.length];
//        }
//
//        // 设置第一组数据源为虚线
////        ((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
////        ((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
////        ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);
//
//        LineData data = new LineData(d,d1);
//        chart.setData(data);
//        chart.invalidate();
//    }

    public void setData(ArrayList<Entry> left, ArrayList<Entry> right, ArrayList<String> ReTime) {

        LineDataSet set1, set2;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            //   set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(left);
            set2.setValues(right);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {

            try {
                XAxis xAxis = chart.getXAxis();
                //  final String[] xValues = {"3.14","3.15","3.16","3.17","3.18","3.19","3.20"};
                //      List<String> list=new ArrayList<>();
                /*给X轴设置数据*/
                xAxis.setValueFormatter(new IndexAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return ReTime.get((int) value);

                    }
                });
            } catch (Exception e) {
                e.getMessage();
            }
            // create a dataset and give it a type
            set1 = new LineDataSet(left, "左肢力量(N)");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.parseColor("#F19B08"));

//            set1.setCircleColor(Color.GRAY);//圆圈颜色
            //  set1.setValueTextSize(10f);
            set1.setDrawValues(false);
            set1.setLineWidth(2f);
            set1.setCircleRadius(2f);
            set1.setFillAlpha(65);
           // set1.setFillColor(Color.parseColor("#F19B08"));
            set1.setCircleColor(Color.parseColor("#F19B08"));
           // set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(true);
            set1.setValueFormatter(new LargeValueFormatter("(N)"));
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(right, "右肢力量(N)");
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
            //     set2.setValueTextSize(10f);
            set2.setDrawValues(false);
            set2.setColor(Color.parseColor("#19E4F1"));
            set2.setLineWidth(2f);
            set2.setCircleRadius(2f);
            set2.setFillAlpha(65);
            set2.setCircleColor(Color.parseColor("#19E4F1"));
            set2.setDrawCircleHole(true);
           // set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            // create a data object with the data sets
            LineData data = new LineData(set1, set2);
//            data.setValueTextColor(Color.GRAY);
//            data.setValueTextSize(10f);

            // set data
            chart.setData(data);
            for (ILineDataSet iSet : chart.getData().getDataSets()) {
                LineDataSet set = (LineDataSet) iSet;
                set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            }
            chart.invalidate();
        }
    }
}
