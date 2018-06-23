package com.wiki.searchwikipedia.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.wiki.searchwikipedia.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WikiWebViewFragment extends Fragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.web_view)
    WebView webView;
    @BindView(R.id.news_load_progress)
    ContentLoadingProgressBar loadingProgressBar;

    private String url;

    public static WikiWebViewFragment newInstance(String title){
        WikiWebViewFragment fragment = new WikiWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wiki_web_view_fragment, container, false);
        ButterKnife.bind(this, view);
        return  view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String title = getArguments().getString("title");
        url = "https://en.m.wikipedia.org/wiki/" + title;

        // progress on player resize
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) loadingProgressBar.getLayoutParams();
        loadingProgressBar.measure(0, 0);
        layoutParams.setMargins(0, -(loadingProgressBar.getMeasuredHeight() / 2), 0, 0);
        loadingProgressBar.setLayoutParams(layoutParams);
        loadingProgressBar.setVisibility(View.VISIBLE);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            toolbar.setTitle(title);
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);


        final Activity activity = getActivity();
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingProgressBar.hide();
            }
        });

        webView.loadUrl(url);

        webView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == MotionEvent.ACTION_UP
                    && webView.canGoBack()) {
                handler.sendEmptyMessage(1);
                return true;
            }
            return false;
        });
    }


    private void webViewGoBack(){
        webView.goBack();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    webViewGoBack();
                    break;
            }
        }
    };
}
