package com.todobom.opennotescanner.helpers;

import retrofit2.Call;
import retrofit2.Retrofit;

import static com.todobom.opennotescanner.helpers.Constants.BASE_URL_DRACOON;
import static com.todobom.opennotescanner.helpers.Constants.DRACOON;

// https://square.github.io/retrofit/
public class Upload {

    // https://dracoon.team/public/upload-shares/BDgQx9gw1jGh7MxBE255CzSVPhkIXYWo

    private String uploadType;
    private String address;


    public Upload(String uploadType, String address) {
        this.uploadType = uploadType;
        this.address = address;
    }

    // TODO: 13.02.20 boolean als return
    public void uploadFile(String fileUri) {
        switch (uploadType) {
            case DRACOON:
                uploadFileViaDracoon(fileUri);
                break;
        }
    }

    private void uploadFileViaDracoon(String fileUri) {
        String[] splitAddress = address.split("/");
        String accessKey = splitAddress[splitAddress.length - 1];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_DRACOON)
                .build();

        DracoonService dracoonService = retrofit.create(DracoonService.class);

        Call<CreateShareUploadChannelResponse> call = dracoonService.createUploadChannel(accessKey, "test");
    }
}
