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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.rick.recoveryapp.R;
import com.rick.recoveryapp.entity.HistoryData;

import java.util.ArrayList;

public class HistoryAdpater extends BaseAdapter {

    protected Context context;
    protected ArrayList<HistoryData> list;

    public HistoryAdpater(Context context, ArrayList<HistoryData> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder vh = null;
            if (convertView == null) {
                LayoutInflater minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = minflater.inflate(R.layout.historylistitem, null);
                vh = new ViewHolder();
                vh.hislist_ltem_usercode = convertView.findViewById(R.id.hislist_ltem_usercode);
                vh.hislist_ltem_username = convertView.findViewById(R.id.hislist_ltem_username);
                vh.hislist_ltem_activetype = convertView.findViewById(R.id.hislist_ltem_activetype);
                vh.hislist_ltem_logtime = convertView.findViewById(R.id.hislist_ltem_logtime);//tv_itemlist_admin
                vh.hislist_ltem_bingliCD = convertView.findViewById(R.id.hislist_ltem_bingliCD);//tv_itemlist_admin
                vh.hislist_ltem_timecount = convertView.findViewById(R.id.hislist_ltem_timecount);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            HistoryData pr = list.get(position);
            String TypeStr = "";
            vh.hislist_ltem_usercode.setText(pr.getRecordID());
            vh.hislist_ltem_username.setText(pr.getUserName());
            vh.hislist_ltem_bingliCD.setText(pr.getUserCode());
            vh.hislist_ltem_logtime.setText(pr.getRecordTime());
            vh.hislist_ltem_timecount.setText(pr.getTimeCount());
            switch (pr.getActiviteType()) {

                case "0":
                    TypeStr = "主动模式";
                    break;
                case "1":
                    TypeStr = "被动模式";
                    break;
                case "2":
                    TypeStr = "智能模式";
                    break;
                case "3":
                    TypeStr = "情景模式";
                    break;
            }
            vh.hislist_ltem_activetype.setText(TypeStr);
            return convertView;
        } catch (Exception ex) {
            String string = ex.getMessage();
            string = "";
        }
        return null;
    }

    public static class ViewHolder {
        TextView hislist_ltem_usercode;
        TextView hislist_ltem_username;
        TextView hislist_ltem_bingliCD;
        TextView hislist_ltem_logtime;
        TextView hislist_ltem_timecount;
        TextView hislist_ltem_activetype;
    }
}
