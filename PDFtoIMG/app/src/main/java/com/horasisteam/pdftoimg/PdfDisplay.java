package com.horasisteam.pdftoimg;


import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;

import java.io.File;

/**
 * Created by anthonygriffon on 16/05/2016.
 */
public class PdfDisplay {
    private PdfRenderer pdfFile;
    //private int PageCount;
    //private String prefixSaveImg;
    //private int PageActive;

    public Bitmap GenerateIMGForPage(int pageindex) {
        PdfRenderer.Page page = pdfFile.openPage(pageindex);
        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        return bitmap;
    }

    public PdfDisplay(PdfRenderer pdfFile) {
        this.pdfFile = pdfFile;
    }
}
