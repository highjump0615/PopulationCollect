/**
 * @author LongHu
 * @date 2014.09.20
 * @filename HelpActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.adapter.ExpandableListAdapter;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_BXFGK_ML1;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_BXFGK_ML2;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.C_BXFGK_NR;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;

import java.util.ArrayList;
import java.util.HashMap;

public class HelpActivity extends PermanentActivity implements ExpandableListView.OnChildClickListener {

    private static final String TAG = HelpActivity.class.getSimpleName();

    private ExpandableListAdapter mExpandableListAdapter;
    private ExpandableListView mExpandableListView;

    private ArrayList<String> mDataHeaderList;
    private HashMap<String, ArrayList<String>> mDataChildMap;
    private ArrayList<ArrayList<String>> mHelpIDList;

    private int lastExpandedPosition = -1;

    @Override
    protected void initViews() {
        super.initViews();

        LayoutInflater.from(this).inflate(R.layout.activity_help, mLayoutContainer);

        // preparing list data
        prepareListData();

        // get the ListView
        mExpandableListView = (ExpandableListView) findViewById(R.id.expend_list_help);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    mExpandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        mExpandableListAdapter = new ExpandableListAdapter(this, mDataHeaderList, mDataChildMap);

        // setting list adapter
        mExpandableListView.setAdapter(mExpandableListAdapter);

        // ListView on child click listener
        mExpandableListView.setOnChildClickListener(this);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String help = "";
        MyContentProvider contentProvider = new MyContentProvider();

        Cursor cursor = contentProvider.query(C_BXFGK_NR.CONTENT_URI, null,
                C_BXFGK_NR.EJMLID + "='" + mHelpIDList.get(groupPosition).get(childPosition) + "'", null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                help = cursor.getString(cursor.getColumnIndex(C_BXFGK_NR.NR));
            }

            cursor.close();
        }

        Intent intent = new Intent(this, HelpViewActivity.class);
        intent.putExtra(HelpViewActivity.HELP_EXTRA, help);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

        return false;
    }

    /*
     * Preparing the list data
	 */
    private void prepareListData() {
        mDataHeaderList = new ArrayList<String>();
        mDataChildMap = new HashMap<String, ArrayList<String>>();

        MyContentProvider contentProvider = new MyContentProvider();

        // Adding child data
        Cursor cursor = contentProvider.query(C_BXFGK_ML1.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            mHelpIDList = new ArrayList<ArrayList<String>>();

            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    ArrayList<String> idList = new ArrayList<String>();
                    ArrayList<String> itemList = new ArrayList<String>();

                    String id = cursor.getString(cursor.getColumnIndex(C_BXFGK_ML1.MLID));
                    String header = cursor.getString(cursor.getColumnIndex(C_BXFGK_ML1.MLMC));

                    mDataHeaderList.add(header);

                    Cursor itemCursor = contentProvider.query(C_BXFGK_ML2.CONTENT_URI, null,
                            C_BXFGK_ML2.YJMLID + "='" + id + "'", null, null);
                    if (itemCursor != null) {
                        if (itemCursor.getCount() > 0 && itemCursor.moveToFirst()) {
                            do {
                                idList.add(itemCursor.getString(itemCursor.getColumnIndex(C_BXFGK_ML2.MLID)));
                                itemList.add(itemCursor.getString(itemCursor.getColumnIndex(C_BXFGK_ML2.MLMC)));
                            } while (itemCursor.moveToNext());
                        }

                        itemCursor.close();
                    }

                    mDataChildMap.put(header, itemList);
                    mHelpIDList.add(idList);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }
    }

}
