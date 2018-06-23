package com.wiki.searchwikipedia.main

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.wiki.searchwikipedia.R
import com.wiki.searchwikipedia.eventbus.SearchLaunchEvent
import com.wiki.searchwikipedia.eventbus.WikiPageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .add(R.id.main, SearchScreenFragment())
                .commit()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    @Subscribe
    fun onEvent(event: SearchLaunchEvent){
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main, SearchScreenFragment())
                .addToBackStack(null)
                .commit()
    }

    @Subscribe
    fun onEvent(event: WikiPageEvent){
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.main, WikiWebViewFragment.newInstance(event.title))
                .addToBackStack(null)
                .commit()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
