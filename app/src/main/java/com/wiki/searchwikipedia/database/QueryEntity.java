package com.wiki.searchwikipedia.database;
import io.realm.RealmList;
import io.realm.RealmObject;

public class QueryEntity extends RealmObject {
    private RealmList<SearchPageEntity> pages;
    private RealmList<RedirectEntity> redirects;

    public RealmList<SearchPageEntity> getPages() {
        return pages;
    }

    public RealmList<RedirectEntity> getRedirects() {
        return redirects;
    }
}
