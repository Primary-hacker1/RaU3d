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
package com.rick.recoveryapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;

import com.rick.recoveryapp.adapter.HistoryAdpater;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.databinding.ActivitySettingBinding;
import com.rick.recoveryapp.databinding.FragmentHistoryBinding;
import com.rick.recoveryapp.entity.HistoryData;
import com.rick.recoveryapp.greendao.ActivitRecordDao;
import com.rick.recoveryapp.greendao.entity.ActivitRecord;
import com.rick.recoveryapp.utils.LocalConfig;
import com.xuexiang.xui.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

//implements View.OnClickListener, AdapterView.OnItemClickListener
public class HistoryActivity extends XPageActivity implements AdapterView.OnItemClickListener {

    HistoryAdpater historyAdpater;
    ArrayList<HistoryData> ArrHis;
    FragmentHistoryBinding binding;
    ActivitRecordDao activitRecordDao;
    List<ActivitRecord> recordList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = FragmentHistoryBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            StatusBarUtils.translucent(this);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;

            binding.bindingHistoryList.setOnItemClickListener(this);
            activitRecordDao = LocalConfig.daoSession.getActivitRecordDao();
            Bandin("");
            initClick();
        } catch (Exception e) {
            Log.d("098765432", e.getMessage());
        }
    }

    public void Bandin(String message) {
        try {
            ArrHis = new ArrayList<HistoryData>();
            recordList = activitRecordDao.queryBuilder().where(
                            activitRecordDao.queryBuilder().or(
                                    ActivitRecordDao.Properties.UserName.like("%" + message + "%"),
                                    ActivitRecordDao.Properties.UserNumber.like("%" + message + "%")))
                    .orderDesc(ActivitRecordDao.Properties.RecordTime)
                    .list();

            for (ActivitRecord Rlist : recordList) {
                HistoryData historyData = new HistoryData();
                historyData.setUserCode(Rlist.getUserNumber() + "");
                historyData.setUserName(Rlist.getUserName() + "");
                historyData.setRecordID(Rlist.getID() + "");
                historyData.setRecordTime(Rlist.getRecordTime() + "");
                historyData.setTimeCount(Rlist.getLongTime() + "");
                historyData.setActiviteType(Rlist.getActivtType() + "");
                ArrHis.add(historyData);
            }

            historyAdpater = new HistoryAdpater(context, ArrHis);
            binding.bindingHistoryList.setAdapter(historyAdpater);
        } catch (Exception e) {
            Log.d("98765432", e.getMessage());
        }

    }


    public void initClick() {

        binding.hisImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.hisEtxtUser.getText().toString();
                Bandin(message);
            }
        });

        binding.historyBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminMainActivity.class);
             //   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //  Toast.makeText(LocalConfig.SettingContext,position+"",Toast.LENGTH_LONG).show();
        //setRecordID
        String RecordID = recordList.get(position).getID().toString();
        Intent in = new Intent(context, DataResultsActivity.class);
        in.putExtra("RecordID", RecordID);
        startActivity(in);
    }
}
