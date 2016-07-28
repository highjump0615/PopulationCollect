/**
 * @author LuYongXing
 * @date 2014.09.17
 * @filename BlackListAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.BlackListData;

import java.util.ArrayList;

/**
 * 流动人口适配器
 */
public class BlackListAdapter2 extends ArrayAdapter<BlackListData> {

    private ArrayList<BlackListData> mValues;
    private LayoutInflater mLayoutInflater;

    public BlackListAdapter2(Context context, ArrayList<BlackListData> values) {
        super(context, R.layout.list_blacklist_item2, values);

        mValues = values;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            ViewHolder viewHolder = new ViewHolder();
            rowView = mLayoutInflater.inflate(R.layout.list_blacklist_item2, parent, false);

            if (rowView == null) {
                throw new IllegalArgumentException("Can not inflate using R.layout.list_blacklist_item");
            }

            viewHolder.textNo = (TextView) rowView.findViewById(R.id.text_no);
            viewHolder.textID = (TextView) rowView.findViewById(R.id.text_id);
            viewHolder.textType = (TextView) rowView.findViewById(R.id.text_type);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        BlackListData data = mValues.get(position);

        // 序号
        viewHolder.textNo.setText(String.valueOf(data.no));
        // MD5
        viewHolder.textID.setText(data.id);
        // 人员类型
        viewHolder.textType.setText(data.type);

        return rowView;
    }

    private class ViewHolder {
        public TextView textNo;
        public TextView textID;
        public TextView textType;
    }

}
