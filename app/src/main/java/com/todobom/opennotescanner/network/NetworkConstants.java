package com.todobom.opennotescanner.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkConstants {

    public static Retrofit getRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static DracoonService getDracoonService(Retrofit retrofit) {
        return retrofit.create(DracoonService.class);
    }

}
