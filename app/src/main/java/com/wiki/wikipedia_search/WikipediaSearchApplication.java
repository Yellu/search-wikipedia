package com.wiki.wikipedia_search;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WikipediaSearchApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//realm initialization
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
