/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rick.recoveryapp.utils;

import android.os.CountDownTimer;

public class PeterTimeCountRefresh extends CountDownTimer {

    private OnTimerFinishListener finishListener;
    private OnTimerProgressListener progressListener;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called. 分钟转换成 毫秒
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.计时的时间间隔
     */
    public PeterTimeCountRefresh(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔,要显示的按钮
    }

    @Override
    public void onTick(long millisUntilFinished) {//计时过程显示
        if (progressListener != null) {
            progressListener.onTimerProgress(millisUntilFinished);
        }

    }

    @Override
    public void onFinish() {//计时完毕时触发
        if (finishListener != null) {
            finishListener.onTimerFinish();
        }
    }

    /**
     * 设置timer走完的回调
     */
    public void setOnTimerFinishListener(OnTimerFinishListener finishListener) {
        this.finishListener = finishListener;
    }

    /**
     * 设置监听进度的
     */
    public void setOnTimerProgressListener(OnTimerProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * Timer 执行完成的回调
     */
    public interface OnTimerFinishListener {

        void onTimerFinish();
    }

    /**
     * Timer 进度的监听
     */
    public interface OnTimerProgressListener {

        void onTimerProgress(long timeLong);
    }


}
