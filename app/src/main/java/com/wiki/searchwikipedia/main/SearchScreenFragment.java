package com.wiki.searchwikipedia.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.wiki.searchwikipedia.R;
import com.wiki.searchwikipedia.database.SearchPageEntity;
import com.wiki.searchwikipedia.network.ApiManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchScreenFragment extends Fragment implements SearchView.OnQueryTextListener{
    @BindView(R.id.wiki_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.search) SearchView searchView;
    @BindView(R.id.feed_loader) ContentLoadingProgressBar loader;
    @BindView(R.id.tv_error_message) TextView tvErrorMessage;

    private Realm realm;
    private SearchResultAdapter adapter;
    private RealmResults<SearchPageEntity> pageEntities;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.query_search_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        realm = Realm.getDefaultInstance();

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setTitle(R.string.app_name);
        }
        pageEntities = realm.where(SearchPageEntity.class).findAll();
        if (pageEntities.isEmpty()){
            tvErrorMessage.setVisibility(View.VISIBLE);
        } else {
            tvErrorMessage.setVisibility(View.GONE);
        }

        adapter = new SearchResultAdapter(pageEntities);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(this);
        searchView.setFocusable(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_search).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clear:
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                adapter.updateAdapter(new ArrayList<>());
                adapter.notifyDataSetChanged();
                tvErrorMessage.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchWiki(s);
        hideKeyboard(getActivity(), getView());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchWiki(s);
        return true;
    }

    //search in wiki
    private Call<ResponseBody> request;
    private void searchWiki(String query){
        if(TextUtils.isEmpty(query)){
            return;
        }

        //already requested search, cancel previous request and make new
        if (request != null){
            request.cancel();
        }

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("action", "query");
        queryMap.put("formatversion", 2);
        queryMap.put("prop", "pageimages|pageterms");
        queryMap.put("pilimit", 10);
        queryMap.put("piprop", "thumbnail");
        queryMap.put("wbptterms", "description");
        queryMap.put("redirects", 1);
        queryMap.put("format", "json");
        queryMap.put("generator", "prefixsearch");
        queryMap.put("gpssearch", query);
        queryMap.put("gpslimit", 10);
//        queryMap.put("pithumbsize", 100);
        request = ApiManager.getInstance().searchApi(queryMap);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    List<SearchPageEntity> searchPageEntities = handleResponse(response.body());
                    loadAdapter(searchPageEntities);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }

    private void loadAdapter(List<SearchPageEntity> pages){
        adapter.updateAdapter(pages);
        adapter.notifyDataSetChanged();

        if (pageEntities.isEmpty() && pages.isEmpty()){
          tvErrorMessage.setVisibility(View.VISIBLE);
        } else {
            tvErrorMessage.setVisibility(View.GONE);
        }
    }

    private List<SearchPageEntity> handleResponse(ResponseBody body){
        List<SearchPageEntity> searchPageEntities = new ArrayList<>();
        try {
            if (body == null){
                return searchPageEntities;
            }

            String jsonStr = body.string();
            JSONObject object = new JSONObject(jsonStr);

            JSONObject queryObject = object.optJSONObject("query");
            if (queryObject != null){
                JSONArray pages = queryObject.optJSONArray("pages");
                if (pages != null){
                    realm.beginTransaction();
                    for (int i = 0; i < pages.length(); ++ i){
                        SearchPageEntity searchPageEntity = realm.createOrUpdateObjectFromJson(SearchPageEntity.class, pages.optJSONObject(i));
                        searchPageEntities.add(searchPageEntity);
                    }
                    realm.commitTransaction();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return searchPageEntities;
    }

    private void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}
