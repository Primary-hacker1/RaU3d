///*
// * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// */
//
//package com.rick.recoveryapp.adapter;
//
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//
//import com.rick.recoveryapp.activity.ActiveActivity;
//import com.rick.recoveryapp.fragment.train.ActiveFragemt;
//import com.rick.recoveryapp.fragment.train.IntelligenceFragemt;
//import com.rick.recoveryapp.fragment.train.PassiveFragemt;
//
//public class TrainFragmentPagerAdapter extends FragmentPagerAdapter {
//
//    private final int PAGER_COUNT  = 3;
//    private ActiveFragemt activeFragemt = null;
//    private PassiveFragemt passiveFragemt = null;
//    private IntelligenceFragemt intelligenceFragemt = null;
//
//    public TrainFragmentPagerAdapter(@NonNull FragmentManager fm) {
//        super(fm);
//        try{
//              passiveFragemt = new PassiveFragemt();
//              activeFragemt = new ActiveFragemt();
//              intelligenceFragemt=new IntelligenceFragemt();
//           // intelligenceFragemt = new IntelligenceFragemt();
//        }catch (Exception ex){
//            ex.getMessage();
//        }
//    }
//
//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//
//        Fragment fragment = null;
//        try{
//
//            switch (position) {
////                case ActiveActivity.PAGE_ONE:
////                    fragment =activeFragemt ;
////                    break;
////
////                case ActiveActivity.PAGE_TWO:
////                    fragment = passiveFragemt;
////                    break;
////
////                case ActiveActivity.PAGE_THREE:
////                    fragment = intelligenceFragemt;
////                    break;
//
//            }
//        }catch (Exception ex){
//            ex.getMessage();
//
//        }
//
//        return fragment;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup vg, int position) {
//        return super.instantiateItem(vg, position);
//
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        System.out.println("position Destory" + position);
//        super.destroyItem(container, position, object);
//    }
//
//    @Override
//    public int getCount() {
//        return PAGER_COUNT;
//    }
//}
