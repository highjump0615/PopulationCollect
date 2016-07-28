package com.ry.fileexplorer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoweifukai.LiuDongRenKouCaijiGuanLi.R;

import java.util.ArrayList;

public class FileArrayAdapter extends ArrayAdapter<FileEntry> {

    private Context context;
    private LayoutInflater layoutInflater;
    private int id;
    private ArrayList<FileEntry> fileEntries;

    public FileArrayAdapter(Context context, int textViewResourceId, ArrayList<FileEntry> objects) {
        super(context, textViewResourceId, objects);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
        id = textViewResourceId;
        fileEntries = objects;
    }

    public FileEntry getItem(int i) {
        return fileEntries.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = layoutInflater.inflate(id, null);

            viewHolder = new ViewHolder();
            viewHolder.textName = (TextView) view.findViewById(R.id.TextView01);
            viewHolder.textData = (TextView) view.findViewById(R.id.TextView02);
            viewHolder.textDate = (TextView) view.findViewById(R.id.TextViewDate);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
               
        final FileEntry o = fileEntries.get(position);

        if (o != null) {
            /* Take the ImageView from layout and set the folder/file's image */
            ImageView imageCity = (ImageView) view.findViewById(R.id.image_icon);
            String uri = "drawable/" + o.image;

            int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            Drawable image = context.getResources().getDrawable(imageResource);
            imageCity.setImageDrawable(image);

            if (viewHolder.textName != null)
                viewHolder.textName.setText(o.name);
            if (viewHolder.textData != null)
                viewHolder.textData.setText(o.data);
            if (viewHolder.textDate != null)
                viewHolder.textDate.setText(o.date);
        }

        return view;
    }

    private class ViewHolder {
        public TextView textName;
        public TextView textData;
        public TextView textDate;
    }

}
