package com.todobom.opennotescanner.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.todobom.opennotescanner.R;
import com.todobom.opennotescanner.network.models.dracoon.ChunkUploadResponse;
import com.todobom.opennotescanner.network.models.dracoon.CreateShareUploadChannelRequest;
import com.todobom.opennotescanner.network.models.dracoon.CreateShareUploadChannelResponse;
import com.todobom.opennotescanner.network.models.dracoon.PublicUploadedFileData;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// https://square.github.io/retrofit/
public class Upload {

    private static final String TAG = "Upload";

    private Context context;
    private String uploadType;
    private String address;


    public Upload(Context context, String uploadType, String address) {
        this.context = context;
        this.uploadType = uploadType;
        this.address = address;
    }

    // TODO: 13.02.20 boolean als return
    public void uploadFile(String fileUri) {
        switch (uploadType) {
            case "dracoon":
                uploadFileViaDracoon(fileUri);
                break;
            case "nextcloud":
                uploadFileViaNextcloud(fileUri);
                break;
            case "email":
                uploadFileViaEmail(fileUri);
                break;
            case "ftp_server":
                uploadFileViaFtpServer(fileUri);
                break;
            case "local":
                // todo necessary?
                break;
        }
    }

    private void uploadFileViaFtpServer(String fileUri) {

    }

    private void uploadFileViaEmail(String fileUri) {

    }

    private void uploadFileViaNextcloud(String fileUri) {
        // dummy method
    }

    // API documentation: dracoon.team/api/
    private void uploadFileViaDracoon(String fileUri) {
        String[] splitAddress = address.split("/");
        // schema of an upload link is always: "base.url/api/v4/public/upload-shares/access_key
        String uploadBaseUrl = "https://" + splitAddress[splitAddress.length - 4] + "/api/v4/";
        DracoonService dracoonService = NetworkConstants.getDracoonService(NetworkConstants.getRetrofit(uploadBaseUrl));

        String accessKey = splitAddress[splitAddress.length - 1];
        File file = new File(fileUri);

        // create file upload channel
        // TODO: 20.02.2020 correct filename
        Call<CreateShareUploadChannelResponse> uploadChannel = dracoonService.createUploadChannel(accessKey,
                new CreateShareUploadChannelRequest("test.jpg", file.length(), null, false));

        final String[] uploadId = new String[1];
        uploadChannel.enqueue(new Callback<CreateShareUploadChannelResponse>() {
            // https://stackoverflow.com/questions/36491096/retrofit-multipart-request-required-multipartfile
            // -parameter-file-is-not-pre/
            @Override
            public void onResponse(Call<CreateShareUploadChannelResponse> call,
                                   Response<CreateShareUploadChannelResponse> response) {
                Log.d(TAG, "onCreateUploadChannel: " + response.code());
                if (response.code() != 201) {
                    showToast(false, file);
                }
                CreateShareUploadChannelResponse res = response.body();
                if (res != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    uploadId[0] = res.getUploadId();

                    // upload file
                    Call<ChunkUploadResponse> upload = dracoonService.uploadFile(accessKey, uploadId[0], body);
                    upload.enqueue(new Callback<ChunkUploadResponse>() {
                        @Override
                        public void onResponse(Call<ChunkUploadResponse> call,
                                               Response<ChunkUploadResponse> response) {
                            Log.d(TAG, "onUploadFile:" + response.code());
                            if (response.code() != 201) {
                                showToast(false, file);
                            }

                            // complete file upload
                            Call<PublicUploadedFileData> completeUpload = dracoonService.completeFileUpload(accessKey
                                    , uploadId[0]);
                            completeUpload.enqueue(new Callback<PublicUploadedFileData>() {
                                @Override
                                public void onResponse(Call<PublicUploadedFileData> call,
                                                       Response<PublicUploadedFileData> response) {
                                    Log.d(TAG, "onCompleteFileUpload: " + response.code());
                                    if (response.code() != 201) {
                                        showToast(false, file);
                                    } else {
                                        showToast(true, file);
                                    }
                                }

                                @Override
                                public void onFailure(Call<PublicUploadedFileData> call, Throwable t) {
                                    Log.e(TAG, "onCompleteFileUpload: " + t.getMessage());
                                    showToast(false, file);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ChunkUploadResponse> call, Throwable t) {
                            Log.e(TAG, "onUploadFile: " + t.getMessage());
                            showToast(false, file);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CreateShareUploadChannelResponse> call, Throwable t) {
                Log.e(TAG, "onCreateUploadChannel: " + t.getMessage());
                showToast(false, file);
            }
        });

    }

    private void showToast(boolean isSuccess, File file) {
        Log.e(TAG, "showToast: " + ((isSuccess) ? "success" : "fail"));
        if (isSuccess) {
            Toast.makeText(context, R.string.upload_success_message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.upload_fail_message, Toast.LENGTH_LONG).show();
        }
        if (file.delete()) {
            Log.d(TAG, "showToast: file deleted");
        }
    }
}
