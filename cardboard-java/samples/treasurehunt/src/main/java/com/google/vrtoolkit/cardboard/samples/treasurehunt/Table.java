package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.google.vrtoolkit.cardboard.audio.CardboardAudioEngine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Quentin on 21/05/2016.
 */
public class Table extends ModelObject {

    public FloatBuffer foundColors;

    public Table(){
        super();
        // Model first appears directly in front of user.
        modelPosition = new float[] {0.0f, -1.0f, -1.5f};
    }

    @Override
    public void make(){
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(this.CUBE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        vertices = bbVertices.asFloatBuffer();
        vertices.put(this.CUBE_COORDS);
        vertices.position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(this.CUBE_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        colors = bbColors.asFloatBuffer();
        colors.put(this.CUBE_COLORS);
        colors.position(0);

        ByteBuffer bbFoundColors = ByteBuffer.allocateDirect(this.CUBE_FOUND_COLORS.length * 4);
        bbFoundColors.order(ByteOrder.nativeOrder());
        foundColors = bbFoundColors.asFloatBuffer();
        foundColors.put(this.CUBE_FOUND_COLORS);
        foundColors.position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(this.CUBE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        normals = bbNormals.asFloatBuffer();
        normals.put(this.CUBE_NORMALS);
        normals.position(0);
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

        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(
                positionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0,
                isLookingAtObject(headView) ? foundColors : colors);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 10*6);
        //checkGLError("Drawing cube");
    }

    @Override
    public boolean isLookingAtObject(float[] headView) {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, model, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }

    public static final float[] CUBE_COORDS = new float[] {

            //6 points par face
            //--> 2 triangles par face (2 points communs)
            //x,y,z

            // Front face
            -2.0f, 0.2f, 1.0f,
            -2.0f, -0.2f, 1.0f,
            2.0f, 0.2f, 1.0f,
            -2.0f, -0.2f, 1.0f,
            2.0f, -0.2f, 1.0f,
            2.0f, 0.2f, 1.0f,

            // Right face
            2.0f, 0.2f, 1.0f,
            2.0f, -0.2f, 1.0f,
            2.0f, 0.2f, -1.0f,
            2.0f, -0.2f, 1.0f,
            2.0f, -0.2f, -1.0f,
            2.0f, 0.2f, -1.0f,

            // Back face
            2.0f, 0.2f, -1.0f,
            2.0f, -0.2f, -1.0f,
            -2.0f, 0.2f, -1.0f,
            2.0f, -0.2f, -1.0f,
            -2.0f, -0.2f, -1.0f,
            -2.0f, 0.2f, -1.0f,

            // Left face
            -2.0f, 0.2f, -1.0f,
            -2.0f, -0.2f, -1.0f,
            -2.0f, 0.2f, 1.0f,
            -2.0f, -0.2f, -1.0f,
            -2.0f, -0.2f, 1.0f,
            -2.0f, 0.2f, 1.0f,

            // Top face
            -2.0f, 0.2f, -1.0f,
            -2.0f, 0.2f, 1.0f,
            2.0f, 0.2f, -1.0f,
            -2.0f, 0.2f, 1.0f,
            2.0f, 0.2f, 1.0f,
            2.0f, 0.2f, -1.0f,

            // Bottom face
            2.0f, -0.2f, -1.0f,
            2.0f, -0.2f, 1.0f,
            -2.0f, -0.2f, -1.0f,
            2.0f, -0.2f, 1.0f,
            -2.0f, -0.2f, 1.0f,
            -2.0f, -0.2f, -1.0f,

            // Left foot / Front face
            -2.0f, -0.2f, 1.0f,
            -2.0f, -2.2f, 1.0f,
            -1.5f, -0.2f, 1.0f,
            -2.0f, -2.2f, 1.0f,
            -1.5f, -2.2f, 1.0f,
            -1.5f, -0.2f, 1.0f,

            // Left foot / Right face
            -1.5f, -0.2f, 1.0f,
            -1.5f, -2.2f, 1.0f,
            -1.5f, -0.2f, 0.0f,
            -1.5f, -2.2f, 1.0f,
            -1.5f, -2.2f, 0.0f,
            -1.5f, -0.2f, 0.0f,

            // Right foot / Front face
            2.0f, -0.2f, 1.0f,
            2.0f, -2.2f, 1.0f,
            1.5f, -0.2f, 1.0f,
            2.0f, -2.2f, 1.0f,
            1.5f, -2.2f, 1.0f,
            1.5f, -0.2f, 1.0f,

            // Right foot / Left face
            1.5f, -0.2f, 1.0f,
            1.5f, -2.2f, 1.0f,
            1.5f, -0.2f, 0.0f,
            1.5f, -2.2f, 1.0f,
            1.5f, -2.2f, 0.0f,
            1.5f, -0.2f, 0.0f,
    };

    public static final float[] CUBE_COLORS = new float[] {

            //6 points par face
            //--> 2 striangles par face
            //R,G,B,transparence

            // front, gray
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,

            // right, gray
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,

            // back, gray
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,

            // left, gray
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,
            0.412f, 0.412f, 0.412f, 1.0f,

            // top, gray
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,

            // bottom, gray
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,

            // left foot front, gray
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,

            // left foot right, gray
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,

            // right foot front, gray
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,

            // right foot left, gray
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
            0.412f,  0.412f,  0.412f, 1.0f,
    };

    public static final float[] CUBE_FOUND_COLORS = new float[] {
            // front, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // right, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // back, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // left, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // top, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // bottom, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // left foot front, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // left foot right, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // right foot front, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,

            // right foot left, yellow
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
            1.0f,  0.6523f, 0.0f, 1.0f,
    };

    public static final float[] CUBE_NORMALS = new float[] {

            //6 points par face
            //--> 2 triangles par face
            //x,y,z
            //On sait pas trop encore à quoi ça correspond mais
            //on pense que ce sont les ombres

            // Front face
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            // Right face
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            // Back face
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            // Left face
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            // Top face
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,

            // Bottom face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            // Left foot / Front face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            // Left foot / Right face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            // Right foot / Front face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            // Right foot / Left face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f
    };

}
