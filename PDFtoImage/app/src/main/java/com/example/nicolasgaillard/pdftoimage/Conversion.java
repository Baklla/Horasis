package com.example.nicolasgaillard.pdftoimage;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.util.Observable;

/**
 * Created by NicolasGaillard on 17/05/2016.
 */


public class Conversion extends Observable {

    private PdfRenderer renderer;
    private int currentPage = 0;
    private int nbPage;
    private int zoomvar = 4;
    private boolean visible = false;

    // TODO : Le path est a récuppérer via un intent
    // private String path;

    public Conversion(String path){
        try {
            // this.path = path;
            File f = new File(path);
            // Le path est passé en paramètre du constructeur mais aura été récup via un intent

            this.renderer = new PdfRenderer(ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY));
            this.nbPage = renderer.getPageCount();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap render(){
        int REQ_WIDTH = renderer.openPage(currentPage).getWidth();
        int REQ_HEIGHT = renderer.openPage(currentPage).getHeight();
        // TODO : voir comment bien initialiser ces valeurs
        // On peut essayer comme ça pour le moment.

        Bitmap bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_4444);
        Rect rect = new Rect(0, 0, REQ_WIDTH, REQ_HEIGHT);

        renderer.openPage(currentPage).render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        return bitmap;
    }

    public boolean CanTurnNext() {
        return currentPage < nbPage;
    }

    public boolean CanTurnPrevious() {
        return currentPage > nbPage;
    }

    public boolean NextPage() {
        if (CanTurnNext()){
            currentPage++;
            this.setChanged();
            return true;
        }
        this.notifyObservers();
        return false;
    }

    public boolean PreviousPage() {
        if (CanTurnPrevious()){
            currentPage--;
            this.setChanged();
            return true;
        }
        this.notifyObservers();
        return false;
    }

    public int GetActivePage() {
        return currentPage;
    }

    public boolean Hide() {
        /*
         * On cache l'interface
         */
        if (this.visible == true) {
            this.visible = false;
            this.setChanged();
            this.notifyObservers();
            return true;
        }
        return false;

    }

    public boolean Show() {
        /*
         * On montre l'interface
         */
        if (this.visible == false) {
            this.visible = true;
            this.setChanged();
            this.notifyObservers();
            return true;
        }
        return false;
    }

    public boolean Zoom() {
        /*
         * On zoom l'interface
         */
        if (this.zoomvar < 10) {
            this.zoomvar++;
            this.setChanged();
            this.notifyObservers();
            return true;
        }
        return false;
    }

    public boolean Unzoom() {
        /*
         * On dezoom l'interface
         */
        if (this.zoomvar > 0) {
            this.zoomvar--;
            this.setChanged();
            this.notifyObservers();
            return true;
        }
        return false;
    }

    // TODO : fermer le renderer une fois l'application fermée
    // Il suffira de faire un .close et au pire les ressources st libérés qd l'app se ferme
}
