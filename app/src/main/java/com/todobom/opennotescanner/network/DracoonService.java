package com.todobom.opennotescanner.network;

import com.todobom.opennotescanner.network.models.dracoon.ChunkUploadResponse;
import com.todobom.opennotescanner.network.models.dracoon.CreateShareUploadChannelRequest;
import com.todobom.opennotescanner.network.models.dracoon.CreateShareUploadChannelResponse;
import com.todobom.opennotescanner.network.models.dracoon.PublicUploadedFileData;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

// API documentation: dracoon.team/api/
public interface DracoonService {

    @POST("public/shares/uploads/{access_key}")
    Call<CreateShareUploadChannelResponse> createUploadChannel(@Path("access_key") String accessKey,
                                                               @Body CreateShareUploadChannelRequest request);

    // https://futurestud.io/tutorials/retrofit-2-how-to-upload-files-to-server
    @Multipart
    @POST("public/shares/uploads/{access_key}/{upload_id}")
    Call<ChunkUploadResponse> uploadFile(@Path("access_key") String accessKey, @Path("upload_id") String uploadId,
                                         @Part MultipartBody.Part file);

    @PUT("public/shares/uploads/{access_key}/{upload_id}")
    Call<PublicUploadedFileData> completeFileUpload(@Path("access_key") String accessKey,
                                                    @Path("upload_id") String uploadId);
}
