package com.ry.fileexplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;

public class FileExplorerActivity extends Activity {

    private static final int REQUEST_FILE_PATH = 1111;

    private EditText mEditFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_explorer);
        mEditFilePath = (EditText) findViewById(R.id.editText);
    }

    public void getFile(View view) {
        startActivityForResult(new Intent(this, FileChooser.class), REQUEST_FILE_PATH);
    }

    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // See which child activity is calling us back.
        if (requestCode == REQUEST_FILE_PATH) {
            if (resultCode == RESULT_OK) {
                mEditFilePath.setText(data.getStringExtra(FileChooser.FILE_PATH_PREF));
            }
        }
    }

}
