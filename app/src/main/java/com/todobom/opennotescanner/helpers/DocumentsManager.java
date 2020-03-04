package com.todobom.opennotescanner.helpers;

import android.util.Log;

import org.parceler.Parcel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@Parcel
@SuppressWarnings("WeakerAccess")
public class DocumentsManager {

    private static final String TAG = "DocumentsManager";

    String FOLDER_NAME = "images";
    ArrayList<String> fileUris;
    File directory;
    String pdfFileUri;

    public DocumentsManager() {
    }

    public DocumentsManager(File cacheDir) {
        updateCacheFolder(cacheDir);
        fileUris = new ArrayList<>();
        pdfFileUri = "";
    }

    private void updateCacheFolder(File cacheDir) {
        this.directory = new File(cacheDir, FOLDER_NAME);
        if (!directory.mkdir()) {
            // https://stackoverflow.com/questions/13195797/delete-all-files-in-directory-but-not
            // -directory-one-liner-solution
            for (File file : Objects.requireNonNull(directory.listFiles()))
                file.delete();
        }
    }

    public void retakeScan() {
        fileUris.remove(fileUris.size() - 1);
    }

    public int getPageNumber() {
        return fileUris.size();
    }

    public String createNewFile(String imgSuffix) {
        try {
            String file =
                    File.createTempFile(getPageNumber() + "_img", imgSuffix, directory).getPath();
            fileUris.add(file);
            return file;
        } catch (IOException e) {
            Log.e(TAG, "createNewFile: could not create temp file", e);
            e.printStackTrace();
        }
        return "";
    }

    public String getCurrentFileUri() {
        // TODO: 04.03.2020 extra variable für fileUri?
        return fileUris.get(getPageNumber() - 1);
    }

    public String createPdfTempFile(String fileName) {
        // TODO: 04.03.2020 right file format
        pdfFileUri = directory.getAbsolutePath() + "/" + fileName + ".pdf";

        return pdfFileUri;
    }

    public String getPdfFileUri() {
        return pdfFileUri;
    }
}
