package com.wiki.searchwikipedia.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SearchPageEntity extends RealmObject {
    public int getPageid() {
        return pageid;
    }

    public int getNs() {
        return ns;
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public ThumbnailEntity getThumbnail() {
        return thumbnail;
    }

    public TermsEntity getTerms() {
        return terms;
    }

    @PrimaryKey
    private int  pageid;
    private int ns;
    private String title;
    private int index;
    private ThumbnailEntity thumbnail;
    private TermsEntity terms;

}
