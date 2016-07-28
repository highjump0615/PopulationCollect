/**
 * @author LuYongXing
 * @date 2014.08.18
 * @filename RentRoomAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.RentRoomData;

import java.util.ArrayList;

/**
 * 房屋信息
 */
public class RentRoomAdapter extends ArrayAdapter<RentRoomData> {

    private Context mContext;
    private ArrayList<RentRoomData> mValues;
    private LayoutInflater mLayoutInflater;

    public RentRoomAdapter(Context context, ArrayList<RentRoomData> values) {
        super(context, R.layout.list_rent_room_item, values);

        mContext = context;
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
        RentRoomData data = mValues.get(position);

        // 序号
        viewHolder.textNo.setText(String.valueOf(data.no));
        // 房主姓名
        viewHolder.textName.setText(data.renterName);
        // 所在地详细
        viewHolder.textAddress.setText(data.address);

        return rowView;
    }

    private class ViewHolder {
        public TextView textNo;
        public TextView textName;
        public TextView textAddress;
    }

}
