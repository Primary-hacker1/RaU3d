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
package com.rick.recoveryapp.fragment.setting;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;


import com.rick.recoveryapp.activity.DataResultsActivity;
import com.rick.recoveryapp.adapter.HistoryAdpater;
import com.rick.recoveryapp.databinding.FragmentHistoryBinding;
import com.rick.recoveryapp.entity.HistoryData;
import com.rick.recoveryapp.greendao.ActivitRecordDao;
import com.rick.recoveryapp.greendao.entity.ActivitRecord;
import com.rick.recoveryapp.utils.LocalConfig;

import java.util.ArrayList;
import java.util.List;

//implements View.OnClickListener, AdapterView.OnItemClickListener
public class HistoryFragment extends Fragment implements AdapterView.OnItemClickListener {

    HistoryAdpater historyAdpater;
    ArrayList<HistoryData> ArrHis;
    FragmentHistoryBinding binding;
    ActivitRecordDao activitRecordDao;
    List<ActivitRecord> recordList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(getLayoutInflater(), container, false);
        binding.bindingHistoryList.setOnItemClickListener(this);

        activitRecordDao = LocalConfig.daoSession.getActivitRecordDao();
        Bandin("");
        initClick();
        return binding.getRoot();
    }

    public void Bandin(String message) {
        ArrHis = new ArrayList<HistoryData>();
        recordList = activitRecordDao.queryBuilder().where(
                        activitRecordDao.queryBuilder().or(
                                ActivitRecordDao.Properties.UserName.like("%" + message + "%"),
                                ActivitRecordDao.Properties.UserNumber.like("%" + message + "%")))
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

        historyAdpater = new HistoryAdpater(LocalConfig.SettingContext, ArrHis);
        binding.bindingHistoryList.setAdapter(historyAdpater);
    }


    public void initClick() {

        binding.hisImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.hisEtxtUser.getText().toString();
                Bandin(message);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //  Toast.makeText(LocalConfig.SettingContext,position+"",Toast.LENGTH_LONG).show();
        //setRecordID
        String RecordID = recordList.get(position).getID().toString();
        Intent in = new Intent(LocalConfig.SettingContext, DataResultsActivity.class);
        in.putExtra("RecordID", RecordID);
        startActivity(in);
    }
}
