package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
            System.out.println(path);
            this.renderer = new PdfRenderer(ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY));
            this.nbPage = renderer.getPageCount();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap render(){//final Context context, Feuille obj, int image){
        PdfRenderer.Page page = renderer.openPage(currentPage);
        int REQ_WIDTH = page.getWidth();
        int REQ_HEIGHT = page.getHeight();
        System.out.println(REQ_WIDTH + "x" + REQ_HEIGHT);
        // TODO : voir comment bien initialiser ces valeurs
        // On peut essayer comme ça pour le moment.

        Bitmap bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        Rect rect = new Rect(0, 0, REQ_WIDTH, REQ_HEIGHT);
        page.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        //obj.loadTexture(context, bitmap);
        page.close();
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