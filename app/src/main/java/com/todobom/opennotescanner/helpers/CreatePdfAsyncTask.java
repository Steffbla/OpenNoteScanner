package com.todobom.opennotescanner.helpers;

import android.os.AsyncTask;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

// https://github.com/Swati4star/Images-to-PDF/blob/master/app/src/main/java/swati4star/createpdf/util/CreatePdf.java
public class CreatePdfAsyncTask extends AsyncTask<String, Void, Void> {

    private String folderName;
    private String fileName;

    public CreatePdfAsyncTask(String folderName, String filename) {
        this.folderName = folderName;
        this.fileName = filename;
    }

    @Override
    protected Void doInBackground(String... params) {

        Rectangle pageSize = PageSize.A4;
        Document document = new Document(pageSize);

        Rectangle docRect = document.getPageSize();

        String outputFile = Environment.getExternalStorageDirectory().toString()
                + "/" + folderName + "/DOC-" + System.currentTimeMillis()
                + ".pdf";
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(outputFile));

            document.open();

            Image image = Image.getInstance(fileName);
            image.setBorder(Rectangle.BOX);

            float pageWidth = document.getPageSize().getWidth();
            float pageHeight = document.getPageSize().getHeight();
            image.scaleToFit(pageWidth, pageHeight);

            image.setAbsolutePosition(
                    (docRect.getWidth() - image.getScaledWidth()) / 2,
                    (docRect.getHeight() - image.getScaledHeight()) / 2);

            document.add(image);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}