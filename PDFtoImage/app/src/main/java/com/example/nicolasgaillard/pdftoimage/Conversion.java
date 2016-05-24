package com.example.nicolasgaillard.pdftoimage;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;

/**
 * Created by NicolasGaillard on 17/05/2016.
 */
public class Conversion {

    private PdfRenderer renderer;
    // private String path;
    private int currentPage = 0;
    private int nbPage;

    // TODO : Le path est a récuppérer via un intent

    public Conversion(String path){
        try {
            // this.path = path;
            File f = new File(path);
            this.renderer = new PdfRenderer(ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY));
            nbPage = renderer.getPageCount();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap render(){
        int REQ_WIDTH = renderer.openPage(currentPage).getWidth();
        int REQ_HEIGHT = renderer.openPage(currentPage).getHeight();
        // TODO : voir comment bien initialiser ces valeurs

        Bitmap bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_4444);
        Rect rect = new Rect(0, 0, REQ_WIDTH, REQ_HEIGHT);

        renderer.openPage(currentPage).render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        return bitmap;
    }

    // TODO : fermer le renderer une fois l'application fermée
}
