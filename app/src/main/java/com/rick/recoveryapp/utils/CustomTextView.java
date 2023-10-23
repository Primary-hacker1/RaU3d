package com.rick.recoveryapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;


@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
        init(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /*
     * 定制字体
     * */
    private void init(Context context) {
        try{
            AssetManager assets = context.getAssets();//获取资源文件
            Typeface font = Typeface.createFromAsset(assets,"fonts/Lobster-1.4.otf");
            setTypeface(font);
        }catch(Exception e){
            e.getMessage();

        }

    }


}
