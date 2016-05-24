package com.example.nicolasgaillard.pdftoimage;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by anthonygriffon on 24/05/2016.
 */
public class InterfaceBtn {

        private int pt=40;
        private float v[]={0.0f,0.0f,0.0f};
        private FloatBuffer vertBuff;

        /*
         * Soit on fait un cercle à la dure avec des verticles, soit on fait un carré, plus simpe avec une texture (ronde)
         */


        public InterfaceBtn(){



        }

        public void draw(GLES20 gl){
            // On trace le cercle

        }
}
