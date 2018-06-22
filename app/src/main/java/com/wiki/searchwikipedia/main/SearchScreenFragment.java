package com.wiki.searchwikipedia.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wiki.searchwikipedia.database.SearchResultMainEntity;
import com.wiki.searchwikipedia.network.ApiManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchScreenFragment extends Fragment implements SearchView.OnQueryTextListener{
    SearchView searchView;

    private Realm realm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         realm = Realm.getDefaultInstance();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchWiki(s);
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
        request = ApiManager.getInstance().searchApi(query);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    handleResponse(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }

    private void handleResponse(ResponseBody body){
        try {
            if (body == null){
                return;
            }

            String jsonStr = body.string();
            JSONObject object = new JSONObject(jsonStr);

            realm.beginTransaction();
            SearchResultMainEntity mainEntity = realm.createObjectFromJson(SearchResultMainEntity.class, object);
            realm.commitTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
