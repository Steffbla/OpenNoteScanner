package com.todobom.opennotescanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class PreviewActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnClickListener {

    private static final String TAG = "PreviewActivity";
    SharedPreferences sharedPref;
    private Button exitBtn;
    private Button saveBtn;
    private Button retakeBtn;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        exitBtn = findViewById(R.id.exit_button);
        saveBtn = findViewById(R.id.save_button);
        retakeBtn = findViewById(R.id.retake_button);
        addBtn = findViewById(R.id.add_button);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPref.getString("file_format", "pdf").equals("pdf")) {
            addBtn.setVisibility(View.GONE);
        }

        saveBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit_button:
                startActivity(new Intent(this, OpenNoteScannerActivity.class));
                // TODO: 04.03.2020 delete files
                break;
            case R.id.save_button:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("yo");
                dialog.setTitle("yooo");
                EditText input = new EditText(this);
                input.setPaddingRelative(5, 5, 5, 5);
                dialog.setView(input);
                dialog.setNegativeButton(R.string.answer_cancel, this);
                dialog.setPositiveButton("save", this);
                dialog.show();
                // TODO: 04.03.2020 correct dialog
                break;
            case R.id.retake_button:
                startActivity(new Intent(this, OpenNoteScannerActivity.class));
                // TODO: 04.03.2020 delete file
                break;
            case R.id.add_button:
                Intent intent = new Intent(this, OpenNoteScannerActivity.class);
                // TODO: 04.03.2020  seite +1
                startActivity(intent);
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

    }
}
