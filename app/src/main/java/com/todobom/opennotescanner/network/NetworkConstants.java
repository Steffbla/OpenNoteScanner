package com.todobom.opennotescanner.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkConstants {

    private static final String BASE_URL_DRACOON = "https://dracoon.team/api/v4/";
    static final String DRACOON_SHARE_LINK = "https://dracoon.team/public/upload-shares/BDgQx9gw1jGh7MxBE255CzSVPhkIXYWo";

    // TODO: 13.02.2020 api constants

    // TODO: 13.02.2020 right path
    // TODO: 13.02.2020 consider using jackson factory
    public static Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl(BASE_URL_DRACOON)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static DracoonService getDracoonService(Retrofit retrofit) {
        return retrofit.create(DracoonService.class);
    }

}
