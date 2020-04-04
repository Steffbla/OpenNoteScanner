package com.todobom.opennotescanner.network;

import android.os.Environment;
import android.util.Log;

import com.todobom.opennotescanner.helpers.AppConstants;
import com.todobom.opennotescanner.helpers.OnSaveCompleteListener;
import com.todobom.opennotescanner.network.models.dracoon.ChunkUploadResponse;
import com.todobom.opennotescanner.network.models.dracoon.CreateShareUploadChannelRequest;
import com.todobom.opennotescanner.network.models.dracoon.CreateShareUploadChannelResponse;
import com.todobom.opennotescanner.network.models.dracoon.PublicUploadedFileData;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveOptionManager {

    private static final String TAG = "SaveFile";

    private OnSaveCompleteListener listener;
    private String saveType;
    private String address;


    public SaveOptionManager(OnSaveCompleteListener listener, String saveType, String address) {
        this.listener = listener;
        this.saveType = saveType;
        this.address = address;
    }

    public void saveFile(String fileUri, String fileName) {
        switch (saveType) {
            case AppConstants.DRACOON:
                uploadFileViaDracoon(fileUri, fileName);
                break;
            case AppConstants.NEXTCLOUD:
                uploadFileViaNextcloud(fileUri, fileName);
                break;
            case AppConstants.EMAIL:
                uploadFileViaEmail(fileUri, fileName);
                break;
            case AppConstants.FTP_SERVER:
                uploadFileViaFtpServer(fileUri, fileName);
                break;
            case AppConstants.LOCAL:
                saveFileLocal(fileUri, fileName);
                break;
        }
    }

    // API documentation: dracoon.team/api/
    // https://square.github.io/retrofit/
    private void uploadFileViaDracoon(String fileUri, String fileName) {
        String[] splitAddress = address.split("/");
        // schema of an upload link is always: "base.url/api/v4/public/upload-shares/access_key
        String uploadBaseUrl = "https://" + splitAddress[splitAddress.length - 4] + "/api/v4/";
        DracoonService dracoonService =
                NetworkConstants.getDracoonService(NetworkConstants.getRetrofit(uploadBaseUrl));

        String accessKey = splitAddress[splitAddress.length - 1];
        File file = new File(fileUri);

        // create file upload channel
        Call<CreateShareUploadChannelResponse> uploadChannel =
                dracoonService.createUploadChannel(accessKey,
                        new CreateShareUploadChannelRequest(fileName, file.length(), null, false));

        final String[] uploadId = new String[1];
        uploadChannel.enqueue(new Callback<CreateShareUploadChannelResponse>() {
            // https://stackoverflow.com/questions/36491096/retrofit-multipart-request-required
            // -multipartfile-parameter-file-is-not-pre/
            @Override
            public void onResponse(Call<CreateShareUploadChannelResponse> call,
                                   Response<CreateShareUploadChannelResponse> response) {
                Log.d(TAG, "onCreateUploadChannel: " + response.code());
                if (response.code() != 201) {
                    Log.e(TAG, "onResponse: " + response.raw());
                    listener.saveComplete(false);
                }
                CreateShareUploadChannelResponse res = response.body();
                if (res != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form" +
                            "-data"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file",
                            file.getName(), requestFile);
                    uploadId[0] = res.getUploadId();

                    // upload file
                    Call<ChunkUploadResponse> upload = dracoonService.uploadFile(accessKey,
                            uploadId[0], body);
                    upload.enqueue(new Callback<ChunkUploadResponse>() {
                        @Override
                        public void onResponse(Call<ChunkUploadResponse> call,
                                               Response<ChunkUploadResponse> response) {
                            Log.d(TAG, "onUploadFile:" + response.code());
                            if (response.code() != 201) {
                                Log.e(TAG, "onResponse: " + response.raw());
                                listener.saveComplete(false);
                            }

                            // complete file upload
                            Call<PublicUploadedFileData> completeUpload =
                                    dracoonService.completeFileUpload(accessKey, uploadId[0]);
                            completeUpload.enqueue(new Callback<PublicUploadedFileData>() {
                                @Override
                                public void onResponse(Call<PublicUploadedFileData> call,
                                                       Response<PublicUploadedFileData> response) {
                                    Log.d(TAG, "onCompleteFileUpload: " + response.code());
                                    if (response.code() != 201) {
                                        Log.e(TAG, "onResponse: " + response.raw());
                                        listener.saveComplete(false);
                                    } else {
                                        listener.saveComplete(true);
                                    }
                                }

                                @Override
                                public void onFailure(Call<PublicUploadedFileData> call,
                                                      Throwable t) {
                                    Log.e(TAG, "onCompleteFileUpload: " + t.getMessage());
                                    listener.saveComplete(false);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ChunkUploadResponse> call, Throwable t) {
                            Log.e(TAG, "onUploadFile: " + t.getMessage());
                            listener.saveComplete(false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CreateShareUploadChannelResponse> call, Throwable t) {
                Log.e(TAG, "onCreateUploadChannel: " + t.getMessage());
                listener.saveComplete(false);
            }
        });

    }

    private void uploadFileViaNextcloud(String fileUri, String fileName) {
        // dummy method
        listener.saveComplete(true);
    }

    private void uploadFileViaEmail(String fileUri, String fileName) {
        // dummy method
        listener.saveComplete(true);
    }

    private void uploadFileViaFtpServer(String fileUri, String fileName) {
        // dummy method
        listener.saveComplete(true);
    }

    private void saveFileLocal(String fileUri, String fileName) {
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                address);
        if (folder.mkdirs()) {
            Log.d(TAG, "wrote: created folder " + folder.getPath());
        }
        File file = new File(fileUri);
        File dest = new File(folder + "/" + fileName);
        try {
            FileUtils.moveFile(file, dest);
        } catch (IOException e) {
            Log.e(TAG, "saveFileLocal: ", e);
            e.printStackTrace();
        }
        listener.saveComplete(true);
    }
}
