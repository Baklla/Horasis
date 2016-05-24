package com.example.nicolasgaillard.pdftoimage;

/**
 * Created by anthonygriffon on 24/05/2016.
 */
public class Controller {
    private Conversion pdf;
    //private Vue vue;

    public Controller(Conversion pdf) {
        this.pdf = pdf;
    }

    public boolean NextPage() {
        if (pdf.NextPage() == true) {
            // On affecte la vue pour tourner la page.
        }
        return false;
    }
}
