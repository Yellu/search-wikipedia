package com.wiki.searchwikipedia.network;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public class ApiManager {
    private static final ApiManager instance = new ApiManager();

    private final String baseUrl = "http://en.wikipedia.org/";
//    private final static String api = "api.php?action=query&formatversion=2&prop=pageimages|pageterms&pilimit=3&piprop=thumbnail&wbptterms=description&redirects= 1";

    public static ApiManager getInstance(){
        return instance;
    }

    private <T> T createRetrofitService(Class<T> service){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(MyLoggingInterceptor.provideOkHttpLogging());

        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }

    private WikiApiClient getService(){
        return createRetrofitService(WikiApiClient.class);
    }

    private interface WikiApiClient{

        @GET("w/api.php")
        Call<ResponseBody> search(@QueryMap Map<String, Object> queryMap);

    }

    public Call<ResponseBody> searchApi(Map<String, Object> queryMap){
        return getService().search(queryMap);
    }

}
