/**
 * @author LuYongXing
 * @date 2014.09.03
 * @filename DistrictStatisticsAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.DistrictStatisticsData;

import java.util.ArrayList;

/**
 * 按辖区采集统计适配器
 */
public class DistrictStatisticsAdapter extends ArrayAdapter<DistrictStatisticsData> {

    private ArrayList<DistrictStatisticsData> mValues;
    private LayoutInflater mLayoutInflater;

    public DistrictStatisticsAdapter(Context context, ArrayList<DistrictStatisticsData> values) {
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
        DistrictStatisticsData data = mValues.get(position);

        // 辖区名称
        viewHolder.textFirst.setText(String.valueOf(data.name));
        // 采集数量
        viewHolder.textSecond.setText(String.valueOf(data.count));
        // 曹主人（民警）姓名
        viewHolder.textThird.setText(data.policeName);

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
