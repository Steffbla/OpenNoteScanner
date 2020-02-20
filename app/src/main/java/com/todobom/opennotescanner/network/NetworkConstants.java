package com.todobom.opennotescanner.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkConstants {

    // TODO: 13.02.2020 api constants
    // TODO: 13.02.2020 right path
    // TODO: 13.02.2020 consider using jackson factory
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
