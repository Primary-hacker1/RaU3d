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

package com.rick.recoveryapp.entity;


import java.util.ArrayList;

public class EcgData {

    private Integer drawColor;
    private Integer index;
    private Boolean isNewStart;
    private Boolean isNoise;
    private Boolean isRPeak;
    private Integer type;
    private Integer coorX;
    private Float coorY;
    private Integer diffX;
    private Integer diffY;
    private Boolean isIdle;
    private Integer originX;
    private Integer originY;
    ArrayList<Float> EcgCoorY;
    private String DataLeng;

    public String getDataLeng() {
        return DataLeng;
    }

    public void setDataLeng(String dataLeng) {
        DataLeng = dataLeng;
    }

    public ArrayList<Float> getEcgCoorY() {
        return EcgCoorY;
    }

    public void setEcgCoorY(ArrayList<Float> ecgCoorY) {
        EcgCoorY = ecgCoorY;
    }

    public String getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(String heartrate) {
        this.heartrate = heartrate;
    }

    private String heartrate;

    public Integer getDrawColor() {
        return drawColor;
    }

    public void setDrawColor(Integer drawColor) {
        this.drawColor = drawColor;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getNewStart() {
        return isNewStart;
    }

    public void setNewStart(Boolean newStart) {
        isNewStart = newStart;
    }

    public Boolean getNoise() {
        return isNoise;
    }

    public void setNoise(Boolean noise) {
        isNoise = noise;
    }

    public Boolean getRPeak() {
        return isRPeak;
    }

    public void setRPeak(Boolean RPeak) {
        isRPeak = RPeak;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCoorX() {
        return coorX;
    }

    public void setCoorX(Integer coorX) {
        this.coorX = coorX;
    }

    public Float getCoorY() {
        return coorY;
    }

    public void setCoorY(Float coorY) {
        this.coorY = coorY;
    }

    public Integer getDiffX() {
        return diffX;
    }

    public void setDiffX(Integer diffX) {
        this.diffX = diffX;
    }

    public Integer getDiffY() {
        return diffY;
    }

    public void setDiffY(Integer diffY) {
        this.diffY = diffY;
    }

    public Boolean getIdle() {
        return isIdle;
    }

    public void setIdle(Boolean idle) {
        isIdle = idle;
    }

    public Integer getOriginX() {
        return originX;
    }

    public void setOriginX(Integer originX) {
        this.originX = originX;
    }

    public Integer getOriginY() {
        return originY;
    }

    public void setOriginY(Integer originY) {
        this.originY = originY;
    }
}
