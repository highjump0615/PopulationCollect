package com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mLayoutInflater;

    // header titles
    private ArrayList<String> mDataHeaderList;

    // child data in format of header title, child title
    private HashMap<String, ArrayList<String>> mDataChildMap;

    public ExpandableListAdapter(Context context, ArrayList<String> listDataHeader,
                                 HashMap<String, ArrayList<String>> listChildData) {
        this.mLayoutInflater = LayoutInflater.from(context);

        this.mDataHeaderList = listDataHeader;
        this.mDataChildMap = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mDataChildMap.get(this.mDataHeaderList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.text_item);
        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count = 0;

        if (mDataChildMap != null) {
            if (this.mDataChildMap.get(this.mDataHeaderList.get(groupPosition)) != null)
                count = this.mDataChildMap.get(this.mDataHeaderList.get(groupPosition)).size();
        }

        return count;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mDataHeaderList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        int count = 0;

        if (mDataHeaderList != null) count = mDataHeaderList.size();

        return count;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_group, null);
        }

        ImageView imageState = (ImageView) convertView.findViewById(R.id.image_state);
        if (getChildrenCount(groupPosition) == 0) {
            imageState.setImageDrawable(null);
        } else {
            if (isExpanded) {
                imageState.setImageResource(R.drawable.ic_action_expand);
            } else {
                imageState.setImageResource(R.drawable.ic_action_collapse2);
            }
        }

        TextView textGroupTitle = (TextView) convertView.findViewById(R.id.text_group_item);
        textGroupTitle.setTypeface(null, Typeface.BOLD);
        textGroupTitle.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return mDataChildMap.get(mDataHeaderList.get(groupPosition)).size() > 0;
    }

}
