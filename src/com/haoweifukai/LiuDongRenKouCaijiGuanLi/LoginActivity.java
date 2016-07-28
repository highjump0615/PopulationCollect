/**
 * @author LuYongXing
 * @date 2014.09.02
 * @filename LoginActivity.java
 */

package com.haoweifukai.LiuDongRenKouCaijiGuanLi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.JiZhuColumns;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.providers.MyContentProvider;
import com.haoweifukai.LiuDongRenKouCaijiGuanLi.utils.CommonUtils;

public class LoginActivity extends PermanentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 检查代码字典表存在有无
        if (!new MyContentProvider().checkConstantTablesExist()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name)
                    .setMessage("代码字典表不存在，请导入数据！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            onBackPressed();
                        }
                    })
                    .create().show();
        }
        // 检查用户情报存在有无
        else if (!checkAccountExists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name)
                    .setMessage("使用采集系统，您必须添加一个用户。请添加用户信息！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(LoginActivity.this, ManageAccountActivity.class));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            onBackPressed();
                        }
                    })
                    .create().show();
        }
        // 移动主界面
        else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }
    }

    /**
     * 检查有无系统用户
     */
    private boolean checkAccountExists() {
        MyContentProvider contentProvider = new MyContentProvider();
        long count = contentProvider.fetchCount(
                CommonUtils.getLastPathFromUri(JiZhuColumns.CONTENT_URI), "");

        return count > 0;
    }

}
