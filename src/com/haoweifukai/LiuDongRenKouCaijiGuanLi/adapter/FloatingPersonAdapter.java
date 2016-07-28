/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename FloatingPersonAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.FloatingPersonData;

import java.util.ArrayList;

/**
 * 流动人口适配器
 */
public class FloatingPersonAdapter extends ArrayAdapter<FloatingPersonData> {

    private ArrayList<FloatingPersonData> mValues;
    private LayoutInflater mLayoutInflater;

    public FloatingPersonAdapter(Context context, ArrayList<FloatingPersonData> values) {
        super(context, R.layout.list_rent_room_item, values);

        mValues = values;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            ViewHolder viewHolder = new ViewHolder();
            rowView = mLayoutInflater.inflate(R.layout.list_rent_room_item, parent, false);

            if (rowView == null) {
                throw new IllegalArgumentException("Can not inflate using R.layout.list_rent_room_item");
            }

            viewHolder.textNo = (TextView) rowView.findViewById(R.id.text_no);
            viewHolder.textName = (TextView) rowView.findViewById(R.id.text_name);
            viewHolder.textAddress = (TextView) rowView.findViewById(R.id.text_address);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        FloatingPersonData data = mValues.get(position);

        // 序号
        viewHolder.textNo.setText(String.valueOf(data.no));
        // 人员姓名
        viewHolder.textName.setText(data.renterName);
        // 现住地详细地址
        viewHolder.textAddress.setText(data.address);

        return rowView;
    }

    private class ViewHolder {
        public TextView textNo;
        public TextView textName;
        public TextView textAddress;
    }

}
