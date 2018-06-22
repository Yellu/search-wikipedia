package com.wiki.searchwikipedia.network;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ApiManager {
    private static final ApiManager instance = new ApiManager();

    private final String baseUrl = "";

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

        @GET
        Call<ResponseBody> search(@Query("q") String query);

    }

    public Call<ResponseBody> searchApi(String query){
        return getService().search(query);
    }

}
