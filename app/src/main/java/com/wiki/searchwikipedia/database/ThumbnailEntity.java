package com.wiki.searchwikipedia.database;

import io.realm.RealmObject;

public class ThumbnailEntity extends RealmObject {
    private String source;
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getSource() {
        return source;
    }
}
