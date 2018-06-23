package com.wiki.searchwikipedia.database;

import io.realm.RealmObject;

public class SearchResultMainEntity extends RealmObject {
    private boolean batchcomplete;
    private QueryEntity query;

    public boolean isBatchcomplete() {
        return batchcomplete;
    }

    public QueryEntity getQuery() {
        return query;
    }
}
