/**
 * @author LuYongXing
 * @date 2014.09.26
 * @filename PreviewActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;

public class PreviewPhotoActivity extends PermanentActivity {

    public static final String PHOTO_ID_ARRAY = "photo_id_array";
    public static final String ENABLE_DELETE = "enable_delete";

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    private PageIndicator mIndicator;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;

    private ArrayList<Uri> mImageUriArray = new ArrayList<Uri>();
    private int mCurrentPos;

    private boolean mEnableDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(PHOTO_ID_ARRAY)) {
            mImageUriArray = (ArrayList<Uri>) intent.getSerializableExtra(PHOTO_ID_ARRAY);
            mEnableDelete = intent.getBooleanExtra(ENABLE_DELETE, false);
        }

        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (mEnableDelete) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(PHOTO_ID_ARRAY, mImageUriArray);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.activity_preview_photo, mLayoutContainer);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mIndicator = (PageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mCurrentPos = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        Button buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(PreviewPhotoActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage("您真的删除此照片吗？")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mImageUriArray.remove(mCurrentPos);

                                if (mImageUriArray.size() == 0) {
                                    onBackPressed();
                                } else {
                                    mPagerAdapter.notifyDataSetChanged();
                                    mIndicator.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
            }
        });

        if (!mEnableDelete) buttonDelete.setVisibility(View.GONE);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.create(mImageUriArray.get(position).toString());
        }

        @Override
        public int getIconResId(int index) {
            return 0;
        }

        @Override
        public int getCount() {
            return mImageUriArray.size();
        }

        @Override
        public int getItemPosition(Object object) {
            int index = mImageUriArray.indexOf(object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }
    }

}
