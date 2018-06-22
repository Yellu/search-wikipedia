package com.wiki.searchwikipedia.database;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TermsEntity extends RealmObject {
    private RealmList<String> description;

    public RealmList<String> getDescription() {
        return description;
    }
}
