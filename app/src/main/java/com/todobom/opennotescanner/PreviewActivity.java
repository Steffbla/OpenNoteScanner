package com.todobom.opennotescanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PreviewActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnClickListener, OnUploadCompleteListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = "PreviewActivity";
    SharedPreferences sharedPref;
    private Button exitBtn;
    private Button saveBtn;
    private Button retakeBtn;
    private Button addBtn;
    EditText fileNameEt;
    private ImageView previewImg;
    private DocumentsManager documentsManager;
    private String fileFormat;
    private String pageSizePref;
    private String[] pageSizeValues;

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
        fileFormat = sharedPref.getString("file_format", ".pdf");
        pageSizePref = sharedPref.getString("page_size", "A4");
        pageSizeValues = getResources().getStringArray(R.array.file_size_values);

        if (!fileFormat.equals(".pdf")) {
            addBtn.setVisibility(View.GONE);
        }
    }

    private void createSaveDialog() {
        // https://bhavyanshu.me/tutorials/create-custom-alert-dialog-in-android/08/20/2015/
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.save_dialog, null);
        dialogBuilder.setView(dialogView);

        fileNameEt = dialogView.findViewById(R.id.et_filename);
        String currentDate = SimpleDateFormat.getDateTimeInstance().format(new Date());
        fileNameEt.setText(getString(R.string.file_name_default, currentDate));
        // pageSize can only be set if it is pdf
        if (fileFormat.equals(".pdf")) {
            Spinner pageSizeSpinner = dialogView.findViewById(R.id.sp_format_size);
            TextView pageSizeTv = dialogView.findViewById(R.id.tv_dialog_page_size_title);
            pageSizeTv.setVisibility(View.VISIBLE);
            pageSizeSpinner.setVisibility(View.VISIBLE);
            // https://developer.android.com/guide/topics/ui/controls/spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.file_size_entries, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pageSizeSpinner.setOnItemSelectedListener(this);
            pageSizeSpinner.setAdapter(adapter);
            pageSizeSpinner.setSelection(getFormatSizePosition());
        }

        dialogBuilder.setTitle(R.string.save_dialog_message);
        dialogBuilder.setPositiveButton(R.string.save_dialog_positive, this);
        dialogBuilder.setNegativeButton(android.R.string.cancel, this);
        TextView formatSize = dialogView.findViewById(R.id.tv_dialog_file_format);
        formatSize.setText(fileFormat);

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private int getFormatSizePosition() {
        for (int i = 0; i < pageSizeValues.length; i++) {
            if (pageSizePref.equals(pageSizeValues[i])) {
                return i;
            }
        }
        return 0;
    }

    private void saveDocument() {
        Log.d(TAG, "saveDocument: ");

        String fileName = fileNameEt.getText().toString();
        ArrayList<String> fileUris = documentsManager.getFileUris();

        if (fileFormat.equals(".pdf")) {
            // https://github.com/Swati4star/Images-to-PDF/
            Rectangle pageSize = PageSize.getRectangle(pageSizePref);
            // https://stackoverflow.com/questions/17274618/itext-landscape-orientation-and
            // -positioning
            if (pageSizePref.contains("landscape")) {
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

                    if (pageSizePref.contains("landscape")) {
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_button:
                startIntentToCameraActivity(new DocumentsManager(getCacheDir()));
                break;
            case R.id.save_button:
                createSaveDialog();
                break;
            case R.id.retake_button:
                documentsManager.retakeScan();
                startIntentToCameraActivity(documentsManager);
                break;
            case R.id.add_button:
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

    @Override
    public void completeUpload() {
        Log.d(TAG, "completeUpload: ");
        startIntentToCameraActivity(documentsManager);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "Spinner onItemSelected: i");
        pageSizePref = pageSizeValues[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "onNothingSelected: ");
    }
}
