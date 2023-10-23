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

package com.rick.recoveryapp.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rick.recoveryapp.fragment.setting.HistoryFragment;
import com.rick.recoveryapp.fragment.setting.SettingFragment;
import com.rick.recoveryapp.fragment.setting.SettingLoginFragement;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 2;//page页数量
    // private HistoryFragment historyFragment = null;
    private SettingFragment settingFragment = null;
    private SettingLoginFragement settingLoginFragement = null;

    public MyFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
//        historyFragment = new HistoryFragment();
        settingFragment = new SettingFragment();
        settingLoginFragement = new SettingLoginFragement();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = settingFragment;
                break;

            case 1:
                fragment = settingLoginFragement;
                break;

        }
        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
