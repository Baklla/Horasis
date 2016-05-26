package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.audio.CardboardAudioEngine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Quentin on 27/05/2016.
 */
public class Murs extends ModelObject {

    public Murs(){
        super();
        modelPosition = new float[] {0.0f, -1.0f, 0.0f};
    }

    @Override
    public void make(){
        // make a floor
        ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(this.MURS_COORDS.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        vertices = bbFloorVertices.asFloatBuffer();
        vertices.put(this.MURS_COORDS);
        vertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(this.MURS_NORMALS.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        normals = bbFloorNormals.asFloatBuffer();
        normals.put(this.MURS_NORMALS);
        normals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(this.MURS_COLORS.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        colors = bbFloorColors.asFloatBuffer();
        colors.put(this.MURS_COLORS);
        colors.position(0);
    }

    @Override
    public void program(int vertexShader, int passthroughShader){
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, passthroughShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        //checkGLError("Cube program");

        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
        colorParam = GLES20.glGetAttribLocation(program, "a_Color");

        modelParam = GLES20.glGetUniformLocation(program, "u_Model");
        modelViewParam = GLES20.glGetUniformLocation(program, "u_MVMatrix");
        modelViewProjectionParam = GLES20.glGetUniformLocation(program, "u_MVP");
        lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos");

        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(normalParam);
        GLES20.glEnableVertexAttribArray(colorParam);

        //checkGLError("Cube program params");
    }

    @Override
    public void updateModelPosition(float[] modelPosition, int soundId, CardboardAudioEngine cardboardAudioEngine) {
        Matrix.setIdentityM(model,0);
        Matrix.translateM(model, 0, modelPosition[0], modelPosition[1], modelPosition[2]);

        // Update the sound location to match it with the new cube position.
        if (soundId != CardboardAudioEngine.INVALID_ID) {
            cardboardAudioEngine.setSoundObjectPosition(
                    soundId, modelPosition[0], modelPosition[1], modelPosition[2]);
        }
        //checkGLError("updateCubePosition");
    }

    @Override
    public void draw(float[] lightPosInEyeSpace, float[] modelView, float[] modelViewProjection, float[] headView) {
        GLES20.glUseProgram(program);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);
        GLES20.glVertexAttribPointer(positionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, colors);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 30);
    }

    @Override
    public boolean isLookingAtObject(float[] headView) { return false; }

    public static final float[] MURS_COORDS = new float[] {
            // Front face
            -5.0f, 3.0f, -5.0f,
            -5.0f, 0.0f, -5.0f,
            5.0f, 3.0f, -5.0f,
            -5.0f, 0.0f, -5.0f,
            5.0f, 0.0f, -5.0f,
            5.0f, 3.0f, -5.0f,

            // Top Face
            -5.0f, 3.0f, -5.0f,
            -5.0f, 3.0f, 5.0f,
            5.0f, 3.0f, -5.0f,
            -5.0f, 3.0f, 5.0f,
            5.0f, 3.0f, 5.0f,
            5.0f, 3.0f, -5.0f,

            // Right face
            5.0f, 3.0f, -5.0f,
            5.0f, 0.0f, -5.0f,
            5.0f, 3.0f, 5.0f,
            5.0f, 0.0f, -5.0f,
            5.0f, 0.0f, 5.0f,
            5.0f, 3.0f, 5.0f,

            // Left face
            -5.0f, 3.0f, 5.0f,
            -5.0f, 0.0f, 5.0f,
            -5.0f, 3.0f, -5.0f,
            -5.0f, 0.0f, 5.0f,
            -5.0f, 0.0f, -5.0f,
            -5.0f, 3.0f, -5.0f,

            // Back face
            -5.0f, 3.0f, 5.0f,
            -5.0f, 0.0f, 5.0f,
            5.0f, 3.0f, 5.0f,
            -5.0f, 0.0f, 5.0f,
            5.0f, 0.0f, 5.0f,
            5.0f, 3.0f, 5.0f,
    };

    public static final float[] MURS_NORMALS = new float[] {
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
    };

    public static final float[] MURS_COLORS = new float[] {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,

            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
    };
}
