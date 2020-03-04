package com.todobom.opennotescanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.todobom.opennotescanner.helpers.DocumentsManager;
import com.todobom.opennotescanner.helpers.OnUploadCompleteListener;
import com.todobom.opennotescanner.network.Upload;

import org.parceler.Parcels;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class PreviewActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnClickListener, OnUploadCompleteListener {

    private static final String TAG = "PreviewActivity";
    SharedPreferences sharedPref;
    private Button exitBtn;
    private Button saveBtn;
    private Button retakeBtn;
    private Button addBtn;
    EditText nameEditText;
    private ImageView previewImg;
    private DocumentsManager documentsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Intent intent = getIntent();
        documentsManager = Parcels.unwrap(intent.getParcelableExtra("documents"));

        previewImg = findViewById(R.id.preview_image);
        previewImg.setImageURI(Uri.parse(documentsManager.getCurrentFileUri()));

        exitBtn = findViewById(R.id.exit_button);
        exitBtn.setOnClickListener(this);
        saveBtn = findViewById(R.id.save_button);
        saveBtn.setOnClickListener(this);
        retakeBtn = findViewById(R.id.retake_button);
        retakeBtn.setOnClickListener(this);
        addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPref.getString("file_format", ".pdf").equals(".pdf")) {
            addBtn.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_button:
                startIntentToCameraActivity(new DocumentsManager(getCacheDir()));
                // TODO: 04.03.2020 delete files
                break;
            case R.id.save_button:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("yo");
                dialog.setTitle("yooo");
                // TODO: 04.03.2020 context
                Spinner sizeSpinner = new Spinner(dialog.getContext());
                dialog.setView(sizeSpinner);
                nameEditText = new EditText(dialog.getContext());
                nameEditText.setPaddingRelative(5, 5, 5, 5);
                dialog.setView(nameEditText);
                dialog.setNegativeButton(R.string.answer_cancel, this);
                dialog.setPositiveButton("save", this);
                dialog.show();
                // TODO: 04.03.2020 correct dialog
                break;
            case R.id.retake_button:
                documentsManager.retakeScan();
                startIntentToCameraActivity(documentsManager);
                // TODO: 04.03.2020 delete file
                break;
            case R.id.add_button:
                // TODO: 04.03.2020 seite +1
                startIntentToCameraActivity(documentsManager);
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialogInterface.cancel();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                saveDocument();
                break;
        }
    }

    private void saveDocument() {
        Log.d(TAG, "saveDocument: ");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String fileName = nameEditText.getText().toString();
        String fileFormat = sharedPref.getString("file_format", ".pdf");
        ArrayList<String> fileUris = documentsManager.getFileUris();

        if (fileFormat.equals(".pdf")) {
            // https://github.com/Swati4star/Images-to-PDF/
            String pageSizePreference = sharedPref.getString("page_size", "A4");
            Rectangle pageSize = PageSize.getRectangle(pageSizePreference);
            // https://stackoverflow.com/questions/17274618/itext-landscape-orientation-and
            // -positioning
            if (pageSizePreference.contains("landscape")) {
                pageSize = pageSize.rotate();
            }
            Document document = new Document(pageSize);

            Rectangle docRect = document.getPageSize();
            String outputFile = documentsManager.createPdfTempFile(fileName);
//                    folder.getAbsolutePath() + "/DOC-" + System.currentTimeMillis() + ".pdf";

            try {
                PdfWriter pdfWriter = PdfWriter.getInstance(document,
                        new FileOutputStream(outputFile));
                document.open();

                for (String uri : fileUris) {
                    Image image = Image.getInstance(uri);
                    image.setBorder(Rectangle.BOX);

                    if (pageSizePreference.contains("landscape")) {
                        image.setRotationDegrees(90);
                    }

                    float pageWidth = document.getPageSize().getWidth();
                    float pageHeight = document.getPageSize().getHeight();
                    image.scaleToFit(pageWidth, pageHeight);

                    image.setAbsolutePosition(
                            (docRect.getWidth() - image.getScaledWidth()) / 2,
                            (docRect.getHeight() - image.getScaledHeight()) / 2);

                    document.add(image);

                    document.newPage();
                }
                document.close();
            } catch (DocumentException | IOException e) {
                Log.e(TAG, "saveDocument: ", e);
                e.printStackTrace();
            }
        }


        String uploadOption = sharedPref.getString("upload_option", "local");
        String uploadAddress = sharedPref.getString("upload_address", "OpenNoteScanner");
        Upload upload = new Upload(this, this, uploadOption, uploadAddress);
        upload.uploadFile(documentsManager.getPdfFileUri(), fileName + fileFormat);
    }

    private void startIntentToCameraActivity(DocumentsManager documentsManager) {
        Intent intent = new Intent(this, OpenNoteScannerActivity.class);
        intent.putExtra("documents", Parcels.wrap(documentsManager));
        startActivity(intent);
    }

    @Override
    public void completeUpload() {
        Log.d(TAG, "completeUpload: ");
        startIntentToCameraActivity(documentsManager);
    }
}
