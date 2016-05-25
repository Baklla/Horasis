package com.google.vrtoolkit.cardboard.samples.treasurehunt;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.audio.CardboardAudioEngine;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by anthonygriffon on 25/05/2016.
 */
public class InterfaceBtn extends ModelObject {

    public static final float[] BTN_COORDS = new float[] {
            -0.5f, 0.211f, -0.7f,
            -0.5f, 0.211f, 0.9f,
            0.5f, 0.211f, -0.7f,
            -0.5f, 0.211f, 0.9f,
            0.5f, 0.211f, 0.9f,
            0.5f, 0.211f, -0.7f,
    };

    public static final float[] BTN_NORMALS = new float[] {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
    };

    public static final float[] BTN_COLORS = new float[] {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
    };

    public static final float[] BTN_TEXTURES = new float[] {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    public float[] btnPosition;
    public FloatBuffer textures;
    public int textureParam;
    public int image;

    public Context context;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** Size of the texture coordinate data in elements. */
    private static final int COORDS_PER_POINT = 2;

    /** This is a handle to our texture data. */
    private int mTextureDataHandle;

    public InterfaceBtn(Context context){
        super();
        this.context=context;
        btnPosition = new float[] {0.0f, -1.0f, -1.5f};
    }

    @Override
    public void make() {
        // make a button
        ByteBuffer bbvertices = ByteBuffer.allocateDirect(this.BTN_COORDS.length * 4);
        bbvertices.order(ByteOrder.nativeOrder());
        vertices = bbvertices.asFloatBuffer();
        vertices.put(this.BTN_COORDS);
        vertices.position(0);

        ByteBuffer bbSheetNormals = ByteBuffer.allocateDirect(this.BTN_NORMALS.length * 4);
        bbSheetNormals.order(ByteOrder.nativeOrder());
        normals = bbSheetNormals.asFloatBuffer();
        normals.put(this.BTN_NORMALS);
        normals.position(0);

        ByteBuffer bbSheetColors = ByteBuffer.allocateDirect(this.BTN_COLORS.length * 4);
        bbSheetColors.order(ByteOrder.nativeOrder());
        colors = bbSheetColors.asFloatBuffer();
        colors.put(this.BTN_COLORS);
        colors.position(0);

        ByteBuffer bbSheetTextures = ByteBuffer.allocateDirect(this.BTN_TEXTURES.length * 4);
        bbSheetTextures.order(ByteOrder.nativeOrder());
        textures = bbSheetTextures.asFloatBuffer();
        textures.put(this.BTN_TEXTURES);
        textures.position(0);
    }

    @Override
    public void program(int vertexShader, int passthroughShader) {
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, passthroughShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
        colorParam = GLES20.glGetAttribLocation(program, "a_Color");
        textureParam = GLES20.glGetAttribLocation(program, "a_TexCoordinate");

        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(normalParam);
        GLES20.glEnableVertexAttribArray(colorParam);
        GLES20.glEnableVertexAttribArray(textureParam);
    }



    @Override
    public void updateModelPosition(float[] modelPosition, int soundId, CardboardAudioEngine cardboardAudioEngine) {

    }

    @Override
    public void draw(float[] lightPosInEyeSpace, float[] modelView, float[] modelViewProjection, float[] headView) {

    }

    @Override
    public boolean isLookingAtObject(float[] modelView, float[] headView) {
        return false;
    }
}
