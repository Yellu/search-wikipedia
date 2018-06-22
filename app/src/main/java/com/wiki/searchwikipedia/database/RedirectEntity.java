package com.wiki.searchwikipedia.database;

import io.realm.RealmObject;

public class RedirectEntity extends RealmObject {
    private int index;
    private String from;
    private String to;

    public int getIndex() {
        return index;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
