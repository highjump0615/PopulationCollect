/**
 * @author LuYongXing
 * @date 2014.09.03
 * @filename LogAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.LogData;

import java.util.ArrayList;

/**
 * 系统日志适配器
 */
public class LogAdapter extends ArrayAdapter<LogData> {

    private ArrayList<LogData> mValues;
    private LayoutInflater mLayoutInflater;

    public LogAdapter(Context context, ArrayList<LogData> values) {
        super(context, R.layout.list_rent_room_item, values);

        mValues = values;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            ViewHolder viewHolder = new ViewHolder();
            rowView = mLayoutInflater.inflate(R.layout.list_system_log_item, parent, false);

            if (rowView == null) {
                throw new IllegalArgumentException("Can not inflate using R.layout.list_system_log_item");
            }

            viewHolder.textNo = (TextView) rowView.findViewById(R.id.text_no);
            viewHolder.textTime = (TextView) rowView.findViewById(R.id.text_time);
            viewHolder.textOperation = (TextView) rowView.findViewById(R.id.text_operation);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        LogData data = mValues.get(position);

        // 序号
        viewHolder.textNo.setText(String.valueOf(data.no));
        // 操作时间
        viewHolder.textTime.setText(data.time);
        // 系统日志纪录
        viewHolder.textOperation.setText(data.operation);

        return rowView;
    }

    private class ViewHolder {
        public TextView textNo;
        public TextView textTime;
        public TextView textOperation;
    }

}
