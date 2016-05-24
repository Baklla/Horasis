package com.example.nicolasgaillard.pdftoimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.media.Image;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends Activity {

    private ImageView imageView;
    private int currentPage = 0;
    private Button next, previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);

        render();

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentPage++;
                render();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentPage--;
                render();
            }
        });

        render();
    }

    private void render() {
        try{
            imageView = (ImageView)findViewById(R.id.image);
            int REQ_WIDTH = imageView.getWidth();
            int REQ_HEIGHT = imageView.getHeight();


            // On prépare le bitmap qui va accueillir la page :
            Bitmap bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_4444);

            // On ouvre le fichier, qui va ensuite être dans un file descriptor pour pouvoir être exploité
            File file = new File("/sdcard/Download/linuxfun.pdf");
            //File file = new File("/storage/emulated/0/Download/SE.pdf");

            // On créé le pdfRenderer,
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

            if(currentPage <= 0) {
                currentPage = 0;
                previous.setEnabled(false);

            } else if(currentPage >= renderer.getPageCount()) {
                currentPage = renderer.getPageCount() - 1;
                next.setEnabled(false);
            }
            else{
                previous.setEnabled(true);
                next.setEnabled(true);
            }

            //Matrix m = imageView.getImageMatrix();
            //Matrix m = new Matrix();
            // C'est la matrice qui est responsable de l'affichage sur une partie de l'écran.

            Rect rect = new Rect(0, 0, REQ_WIDTH, REQ_HEIGHT);

            // On met la page sur le bitmap avec la méthode render()
            renderer.openPage(currentPage).render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // S'arrêter là si on veut travailler sur le bitmap.


           // imageView.setImageMatrix(m);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
