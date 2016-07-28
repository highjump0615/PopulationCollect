/**
 * @author LuYongXing
 * @date 2014.09.10
 * @filename RegisterHistoryAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.RegisterHistoryData;

import java.util.ArrayList;

public class RegisterHistoryAdapter extends ArrayAdapter<RegisterHistoryData> {

    private ArrayList<RegisterHistoryData> mValues;
    private LayoutInflater mLayoutInflater;

    public RegisterHistoryAdapter(Context context, ArrayList<RegisterHistoryData> values) {
        super(context, R.layout.list_rent_room_item, values);

        mValues = values;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            ViewHolder viewHolder = new ViewHolder();
            rowView = mLayoutInflater.inflate(R.layout.list_register_history_item, parent, false);

            if (rowView == null) {
                throw new IllegalArgumentException("Can not inflate using R.layout.list_rent_room_item");
            }

            viewHolder.textNo = (TextView) rowView.findViewById(R.id.text_no);
            viewHolder.textName = (TextView) rowView.findViewById(R.id.text_name);
            viewHolder.textAddress = (TextView) rowView.findViewById(R.id.text_address);
            viewHolder.textDate = (TextView) rowView.findViewById(R.id.text_date);
            viewHolder.textRegisterTime = (TextView) rowView.findViewById(R.id.text_register_time);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        RegisterHistoryData data = mValues.get(position);

        viewHolder.textNo.setText(String.valueOf(data.no));
        viewHolder.textName.setText(data.name);
        viewHolder.textAddress.setText(data.address);
        viewHolder.textDate.setText(data.origin);
        viewHolder.textRegisterTime.setText(data.time);

        return rowView;
    }

    private class ViewHolder {
        public TextView textNo;
        public TextView textName;
        public TextView textAddress;
        public TextView textDate;
        public TextView textRegisterTime;
    }

}
