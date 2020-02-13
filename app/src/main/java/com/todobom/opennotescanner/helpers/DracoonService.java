package com.todobom.opennotescanner.helpers;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DracoonService {

    @POST("public/shares/uploads/{access_key}")
    Call<CreateShareUploadChannelResponse> createUploadChannel(@Path("accessKey") String accessKey, @Body() String name);

    // TODO: 13.02.20 @Mulitpart?
    @POST("public/shares/uploads/{access_key}/{upload_id}")
    Call<ChunkUploadResponse> uploadFile(@Path("accessKey") String accessKey, @Path("upload_id") String uploadId);

    @PUT("public/shares/uploads/{access_key}/{upload_id}")
    Call<PublicUploadedFileData> completeFileUpload(@Path("accessKey") String accessKey, @Path("upload_id")
            String uploadId);
}
