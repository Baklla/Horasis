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
 * Created by Horasis Team <3 on 21/05/2016.
 */
public class Feuille extends ModelObject {

    public float[] sheetPosition;
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

    public Feuille(Context context){
        super();
        this.context=context;
        sheetPosition = new float[] {0.0f, -1.0f, -1.5f};
    }

    @Override
    public void make(){
        // make a sheet
        ByteBuffer bbvertices = ByteBuffer.allocateDirect(this.SHEET_COORDS.length * 4);
        bbvertices.order(ByteOrder.nativeOrder());
        vertices = bbvertices.asFloatBuffer();
        vertices.put(this.SHEET_COORDS);
        vertices.position(0);

        ByteBuffer bbSheetNormals = ByteBuffer.allocateDirect(this.SHEET_NORMALS.length * 4);
        bbSheetNormals.order(ByteOrder.nativeOrder());
        normals = bbSheetNormals.asFloatBuffer();
        normals.put(this.SHEET_NORMALS);
        normals.position(0);

        ByteBuffer bbSheetColors = ByteBuffer.allocateDirect(this.SHEET_COLORS.length * 4);
        bbSheetColors.order(ByteOrder.nativeOrder());
        colors = bbSheetColors.asFloatBuffer();
        colors.put(this.SHEET_COLORS);
        colors.position(0);

        ByteBuffer bbSheetTextures = ByteBuffer.allocateDirect(this.SHEET_TEXTURES.length * 4);
        bbSheetTextures.order(ByteOrder.nativeOrder());
        textures = bbSheetTextures.asFloatBuffer();
        textures.put(this.SHEET_TEXTURES);
        textures.position(0);
    }

    @Override
    public void program(int vertexShader, int passthroughShader){
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, passthroughShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        //checkGLError("Sheet program");

        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
        colorParam = GLES20.glGetAttribLocation(program, "a_Color");

        //modelParam = GLES20.glGetUniformLocation(program, "u_Model");
        modelViewParam = GLES20.glGetUniformLocation(program, "u_MVMatrix");
        modelViewProjectionParam = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
        lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos");

        mTextureUniformHandle = GLES20.glGetUniformLocation(program, "u_Texture");
        textureParam = GLES20.glGetAttribLocation(program, "a_TexCoordinate");

        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(normalParam);
        GLES20.glEnableVertexAttribArray(colorParam);
        GLES20.glEnableVertexAttribArray(textureParam);
        //checkGLError("Sheet program params");
    }

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    @Override
    public void updateModelPosition(float[] modelPosition, int soundId, CardboardAudioEngine cardboardAudioEngine) {
        Matrix.setIdentityM(model,0);
        Matrix.translateM(model, 0, modelPosition[0], modelPosition[1], modelPosition[2]);
        //Matrix.translateM(modelSheet, 0, sheetPosition[0], sheetPosition[1], sheetPosition[2]);

        // Update the sound location to match it with the new cube position.
        if (soundId != CardboardAudioEngine.INVALID_ID) {
            cardboardAudioEngine.setSoundObjectPosition(
                    soundId, modelPosition[0], modelPosition[1], modelPosition[2]);
            //cardboardAudioEngine.setSoundObjectPosition(
                    //soundId, sheetPosition[0], sheetPosition[1], sheetPosition[2]);
        }
        //checkGLError("updateCubePosition");
    }

    @Override
    public void draw(float[] lightPosInEyeSpace, float[] modelView, float[] modelViewProjection, float[] headView) {
        GLES20.glUseProgram(program);

        // Bind the texture to this unit.
        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
        // Enable texture mapping
        //GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        // Load the texture
        mTextureDataHandle = image;
        //GLES20.glDisable(GLES20.GL_TEXTURE_2D);
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //GLES20.glDisable(GLES20.GL_TEXTURE_2D);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);
        // GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);
        GLES20.glVertexAttribPointer(
                positionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, colors);

        GLES20.glVertexAttribPointer(textureParam, COORDS_PER_POINT, GLES20.GL_FLOAT, false, 0, textures);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    @Override
    public boolean isLookingAtObject(float[] modelView, float[] headView) {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, model, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }

    public static final float[] SHEET_COORDS = new float[] {
          /*-0.5f, 2.2f, -1.0f,
          -0.5f, 0.2f, -1.0f,
          0.5f, 2.2f, -1.0f,
          -0.5f, 0.2f, -1.0f,
          0.5f, 0.2f, -1.0f,
          0.5f, 2.2f, -1.0f,*/

            -0.5f, 0.211f, -0.7f,
            -0.5f, 0.211f, 0.9f,
            0.5f, 0.211f, -0.7f,
            -0.5f, 0.211f, 0.9f,
            0.5f, 0.211f, 0.9f,
            0.5f, 0.211f, -0.7f,
    };

    public static final float[] SHEET_NORMALS = new float[] {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
    };

    public static final float[] SHEET_COLORS = new float[] {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
    };

    public static final float[] SHEET_TEXTURES = new float[] {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };
}
