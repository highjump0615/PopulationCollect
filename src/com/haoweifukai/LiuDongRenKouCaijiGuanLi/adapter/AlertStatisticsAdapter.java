/**
 * @author LuYongXing
 * @date 2014.09.17
 * @filename AlertStatisticsAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.AlertStatisticsData;

import java.util.ArrayList;

/**
 * 按预警统计适配器
 */
public class AlertStatisticsAdapter extends ArrayAdapter<AlertStatisticsData> {

    private ArrayList<AlertStatisticsData> mValues;
    private LayoutInflater mLayoutInflater;

    public AlertStatisticsAdapter(Context context, ArrayList<AlertStatisticsData> values) {
        super(context, R.layout.list_statistics_item, values);

        mValues = values;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            ViewHolder viewHolder = new ViewHolder();
            rowView = mLayoutInflater.inflate(R.layout.list_statistics_item, parent, false);

            if (rowView == null) {
                throw new IllegalArgumentException("Can not inflate using R.layout.list_statistics_item");
            }

            viewHolder.textFirst = (TextView) rowView.findViewById(R.id.text_first);
            viewHolder.textSecond = (TextView) rowView.findViewById(R.id.text_second);
            viewHolder.textThird = (TextView) rowView.findViewById(R.id.text_third);
            viewHolder.textFourth = (TextView) rowView.findViewById(R.id.text_fourth);
            viewHolder.textFifth = (TextView) rowView.findViewById(R.id.text_fifth);

            viewHolder.textFourth.setVisibility(View.GONE);
            viewHolder.textFifth.setVisibility(View.GONE);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        AlertStatisticsData data = mValues.get(position);

        // 时间
        viewHolder.textFirst.setText(String.valueOf(data.date));
        // 采集数量
        viewHolder.textSecond.setText(String.valueOf(data.collectionCount));
        // 预警次数
        viewHolder.textThird.setText(String.valueOf(data.alertCount));

        return rowView;
    }

    private class ViewHolder {
        public TextView textFirst;
        public TextView textSecond;
        public TextView textThird;
        public TextView textFourth;
        public TextView textFifth;
    }

}
