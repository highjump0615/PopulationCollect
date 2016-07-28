/**
 * @author LongHu
 * @date 2014.09.28
 * @filename HelpViewActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HelpViewActivity extends PermanentActivity {

    public static final String HELP_EXTRA = "help_extra";

    private String mHelp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra(HELP_EXTRA)) {
            mHelp = intent.getStringExtra(HELP_EXTRA);
        }

        super.onCreate(savedInstanceState);

        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initViews() {
        super.initViews();

        mLayoutInflater.inflate(R.layout.activity_help_view, mLayoutContainer);

        TextView textView = (TextView) findViewById(R.id.text_help);
        textView.setText(mHelp);
    }

}
