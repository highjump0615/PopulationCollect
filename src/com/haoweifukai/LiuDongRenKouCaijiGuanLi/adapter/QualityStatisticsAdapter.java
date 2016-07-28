/**
 * @author LuYongXing
 * @date 2014.09.03
 * @filename QualityStatisticsAdapter.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.data.QualityStatisticsData;

import java.util.ArrayList;

/**
 * 按质量统计适配器
 */
public class QualityStatisticsAdapter extends ArrayAdapter<QualityStatisticsData> {

    private Context mContext;
    private ArrayList<QualityStatisticsData> mValues;
    private LayoutInflater mLayoutInflater;

    public QualityStatisticsAdapter(Context context, ArrayList<QualityStatisticsData> values) {
        super(context, R.layout.list_statistics_item, values);

        mContext = context;
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

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        QualityStatisticsData data = mValues.get(position);

        // 时间
        viewHolder.textFirst.setText(String.valueOf(data.time));
        // 采集数量
        viewHolder.textSecond.setText(String.valueOf(data.count));
        // 非必填字段数量
        viewHolder.textThird.setText(String.valueOf(data.totalFieldsCount));
        // 非必填字段填写数量
        viewHolder.textFourth.setText(String.valueOf(data.filledFieldsCount));
        // 完成百分比
        viewHolder.textFifth.setText(String.valueOf(100 * data.filledFieldsCount / data.totalFieldsCount));

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
