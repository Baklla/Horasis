package com.horasisteam.pdftoimg;

/**
 * Created by anthonygriffon on 16/05/2016.
 */

import android.app.Activity;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class test extends Activity {
    private PdfDisplay pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView img = (ImageView)findViewById(R.id.imageView);
        setContentView(R.layout.view);
        if (savedInstanceState == null) {
            //getFragmentManager().beginTransaction()
            //        .add(R.id.container, new PdfRendererBasicFragment(),
            //                FRAGMENT_PDF_RENDERER_BASIC)
            //        .commit();

            File file = new File("/sdcard/Download/linuxfun.pdf");
            //mFileDescriptor = context.getAssets().openFd("sample.pdf").getParcelFileDescriptor();
            // This is the PdfRenderer we use to render the PDF.
            try {
                pdf = new PdfDisplay(new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)));
                img.setImageBitmap(pdf.GenerateIMGForPage(1));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }



}
