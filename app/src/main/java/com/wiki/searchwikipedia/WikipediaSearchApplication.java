package com.wiki.searchwikipedia;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class WikipediaSearchApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//realm initialization
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .build();
        Realm.setDefaultConfiguration(config);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Muli-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
