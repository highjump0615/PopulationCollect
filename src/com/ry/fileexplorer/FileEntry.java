package com.ry.fileexplorer;

public class FileEntry implements Comparable<FileEntry> {

    // please refer http://developer.android.com/training/articles/perf-tips.html#GettersSetters.
    public String name;
    public String data;
    public String date;
    public String path;
    public String image;

    public FileEntry(String name, String data, String date, String path, String image) {
        this.name = name;
        this.data = data;
        this.date = date;
        this.path = path;
        this.image = image;
    }

    public int compareTo(FileEntry o) {
        if (this.name != null)
            return this.name.toLowerCase().compareTo(o.name.toLowerCase());
        else
            throw new IllegalArgumentException();
    }

}
