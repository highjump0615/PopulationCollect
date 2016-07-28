package com.ry.fileexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class DirectoryChooser extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final boolean SHOW_HIDDEN_FILES = false;

    public static final String DIRECTORY_PATH_PREF = "directory_path";

    private File mCurrentDir;
    private FileArrayAdapter mAdapter;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_directory_chooser);

        mListView = (ListView) findViewById(R.id.list_directory);
        mListView.setOnItemClickListener(this);

        findViewById(R.id.button_ok).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);

        mCurrentDir = new File(loadPreviousPath());
        fill(mCurrentDir);
    }

    @Override
    protected void onDestroy() {
        savePreviousPath();

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                Intent intent = new Intent();
                intent.putExtra(DIRECTORY_PATH_PREF, mCurrentDir.toString());
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.button_cancel:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileEntry entry = mAdapter.getItem(position);
        if (entry.image.equalsIgnoreCase("directory_icon") || entry.image.equalsIgnoreCase("directory_up")) {
            mCurrentDir = new File(entry.path);
            fill(mCurrentDir);
        }
    }

    private void fill(File file) {
        File[] directories;

        if (SHOW_HIDDEN_FILES) {
            directories = file.listFiles();
        } else {
            directories = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return !filename.startsWith(".");
                }
            });
        }

        this.setTitle(file.toString());
        ArrayList<FileEntry> directoryEntry = new ArrayList<FileEntry>();
        ArrayList<FileEntry> fileEntry = new ArrayList<FileEntry>();

        if (directories != null) {
            try {
                for (File f : directories) {
                    Date lastModDate = new Date(f.lastModified());
                    DateFormat formatter = DateFormat.getDateTimeInstance();
                    String date_modify = formatter.format(lastModDate);

                    if (f.isDirectory()) {
                        File[] files;

                        if (SHOW_HIDDEN_FILES) {
                            files = f.listFiles();
                        } else {
                            files = f.listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String filename) {
                                    return !filename.startsWith(".");
                                }
                            });
                        }

                        int itemCount = (files != null) ? files.length : 0;
                        String num_item = String.valueOf(itemCount) + ((itemCount == 0) ? " item" : " items");

                        directoryEntry.add(new FileEntry(f.getName(), num_item, date_modify, f.getAbsolutePath(), "directory_icon"));
                    } else {
                        long fileSize = f.length();
                        fileEntry.add(new FileEntry(f.getName(), fileSize + (fileSize > 0 ? " Bytes" : " Byte"), date_modify, f.getAbsolutePath(), "file_icon"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(directoryEntry);
        Collections.sort(fileEntry);

        directoryEntry.addAll(fileEntry);

        if (!file.getName().equalsIgnoreCase(Environment.getExternalStorageDirectory().getName()))
            directoryEntry.add(0, new FileEntry("..", "Parent Directory", "", file.getParent(), "directory_up"));

        mAdapter = new FileArrayAdapter(DirectoryChooser.this, R.layout.list_file_item, directoryEntry);
        mListView.setAdapter(mAdapter);
    }

    private String loadPreviousPath() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString(DIRECTORY_PATH_PREF, Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void savePreviousPath() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DIRECTORY_PATH_PREF, mCurrentDir.toString());
        editor.apply();
    }

}
