package com.todobom.opennotescanner.helpers;

import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PdfCreator {

    private static final String TAG = "PdfCreator";

    public static void makePdf(String pageSizePref, String outputFile, ArrayList<String> fileUris) {
        // https://github.com/Swati4star/Images-to-PDF/
        Rectangle pageSize = PageSize.getRectangle(pageSizePref);
        // https://stackoverflow.com/questions/17274618/itext-landscape-orientation-and
        // -positioning
        if (pageSizePref.contains("landscape")) {
            pageSize = pageSize.rotate();
        }
        Document document = new Document(pageSize);

        Rectangle docRect = document.getPageSize();
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
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
}
