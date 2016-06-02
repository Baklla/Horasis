/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.audio.CardboardAudioEngine;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.Model.Conversion;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.ButtonNext;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.ButtonPrevious;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.ButtonUnzoom;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.ButtonZoom;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.Feuille;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.Floor;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.InterfaceBtn;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.Murs;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.CardboardEnvironment.View.Models3D.Table;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Cardboard sample application.
 */
public class HorasisCardboardView
    extends CardboardActivity implements CardboardView.StereoRenderer {
  private static final String TAG = "TreasureHuntActivity";

  private static final float Z_NEAR = 0.1f;
  private static final float Z_FAR = 100.0f;

  private float CAMERA_Z = 0.01f;
  private static final float TIME_DELTA = 0.3f;

  // We keep the light always position just above the user.
  private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] {0.0f, 2.0f, 0.0f, 1.0f};

  private static final float MIN_MODEL_DISTANCE = 3.0f;
  private static final float MAX_MODEL_DISTANCE = 7.0f;

  private static final String SOUND_FILE = "cube_sound.wav";

  private final float[] lightPosInEyeSpace = new float[4];

  private float[] camera;
  private float[] view;
  private float[] headView;

  private float[] headRotation;

  private float objectDistance = MAX_MODEL_DISTANCE / 2.0f;

  private Vibrator vibrator;
  private CardboardOverlayView overlayView;

  private CardboardAudioEngine cardboardAudioEngine;
  private volatile int soundId = CardboardAudioEngine.INVALID_ID;

  private Conversion pdf;

  private Murs murs;
  private Floor floor;
  private Table table;
  private Feuille feuille;

  private InterfaceBtn btnNext;
  private InterfaceBtn btnPrevious;
  private InterfaceBtn btnZoom;
  private InterfaceBtn btnUnzoom;

  /**
   * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
   *
   * @param type The type of shader we will be creating.
   * @param resId The resource ID of the raw text file about to be turned into a shader.
   * @return The shader object handler.
   */
  private int loadGLShader(int type, int resId) {
    String code = readRawTextFile(resId);
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, code);
    GLES20.glCompileShader(shader);

    // Get the compilation status.
    final int[] compileStatus = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

    // If the compilation failed, delete the shader.
    if (compileStatus[0] == 0) {
      Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
      GLES20.glDeleteShader(shader);
      shader = 0;
    }

    if (shader == 0) {
      throw new RuntimeException("Error creating shader.");
    }

    return shader;
  }

  /**
   * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
   *
   * @param label Label to report in case of error.
   */
  private static void checkGLError(String label) {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      Log.e(TAG, label + ": glError " + error);
      throw new RuntimeException(label + ": glError " + error);
    }
  }

  /**
   * Sets the view to our CardboardView and initializes the transformation matrices we will use
   * to render our scene.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.pdf = new Conversion(getIntent().getStringExtra("path"));

    setContentView(R.layout.common_ui);

    CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
    cardboardView.setRenderer(this);
    cardboardView.setTransitionViewEnabled(true);
    cardboardView.setOnCardboardBackButtonListener(new Runnable() {
        @Override
        public void run() {
          onBackPressed();
        }
    });
    setCardboardView(cardboardView);

    this.murs = new Murs();
    this.floor = new Floor();
    this.table = new Table();
    this.feuille = new Feuille(this);

    this.btnNext = new ButtonNext(this);
    this.btnPrevious = new ButtonPrevious(this);
    this.btnZoom = new ButtonZoom(this);
    this.btnUnzoom = new ButtonUnzoom(this);

    camera = new float[16];
    view = new float[16];

    //modelPosition = new float[] {0.0f, 0.0f, -MAX_MODEL_DISTANCE / 2.0f};
    headRotation = new float[4];
    headView = new float[16];
    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    // Initialize 3D audio engine.
    cardboardAudioEngine =
        new CardboardAudioEngine(this, CardboardAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);

    overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
    overlayView.show3DImage();
    //overlayView.show3DToast("Pull the magnet when you find an object.");

  }

  @Override
  public void onPause() {
    cardboardAudioEngine.pause();
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    cardboardAudioEngine.resume();
  }

  @Override
  public void onRendererShutdown() {
    Log.i(TAG, "onRendererShutdown");
  }

  @Override
  public void onSurfaceChanged(int width, int height) {
    Log.i(TAG, "onSurfaceChanged");
  }

  /**
   * Creates the buffers we use to store information about the 3D world.
   *
   * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
   * Hence we use ByteBuffers.
   *
   * @param config The EGL configuration used when creating the surface.
   */
  @Override
  public void onSurfaceCreated(EGLConfig config) {
    Log.i(TAG, "onSurfaceCreated");
    GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.

    this.murs.make();
    this.floor.make();
    this.table.make();
    this.feuille.make();
    this.btnNext.make();
    this.btnPrevious.make();
    this.btnZoom.make();
    this.btnUnzoom.make();
    checkGLError("Objects makes");

    int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
    int gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
    int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

    this.murs.program(vertexShader,passthroughShader);
    checkGLError("Floor program");
    checkGLError("Floor program params");

    this.floor.program(vertexShader,gridShader);
    checkGLError("Floor program");
    checkGLError("Floor program params");

    this.table.program(vertexShader,passthroughShader);
    checkGLError("Cube program");
    checkGLError("Cube program params");

    int vertexShader2 = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.test_vertex_shader);
    int passthroughShader2 = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.test_fragment_shader);
    this.feuille.program(vertexShader2,passthroughShader2);
    checkGLError("Sheet program");
    checkGLError("Sheet program params");

    this.btnNext.program(vertexShader2,passthroughShader2);
    checkGLError("BtnNext program");
    checkGLError("BtnNext program params");

    this.btnPrevious.program(vertexShader2,passthroughShader2);
    checkGLError("BtnPrevious program");
    checkGLError("BtnPrevious program params");

    this.btnZoom.program(vertexShader2,passthroughShader2);
    checkGLError("BtnZoom program");
    checkGLError("BtnZoom program params");

    this.btnUnzoom.program(vertexShader2,passthroughShader2);
    checkGLError("BtnUnzoom program");
    checkGLError("BtnUnzoom program params");
/*
    // Avoid any delays during start-up due to decoding of sound files.
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                // Start spatial audio playback of SOUND_FILE at the model postion. The returned
                //soundId handle is stored and allows for repositioning the sound object whenever
                // the cube position changes.
                cardboardAudioEngine.preloadSoundFile(SOUND_FILE);
                soundId = cardboardAudioEngine.createSoundObject(SOUND_FILE);
                cardboardAudioEngine.setSoundObjectPosition(
                    soundId, modelPosition[0], modelPosition[1], modelPosition[2]);
                cardboardAudioEngine.playSound(soundId, true /* looped playback *//*);
              }
            })
        .start();
    */

    this.murs.updateModelPosition(this.murs.modelPosition,soundId,cardboardAudioEngine);

    this.table.updateModelPosition(this.table.modelPosition,soundId,cardboardAudioEngine);

    this.feuille.updateModelPosition(this.feuille.modelPosition,soundId,cardboardAudioEngine);
    this.feuille.image = this.feuille.loadTexture(this, pdf.render());

    this.btnNext.updateModelPosition(this.btnNext.modelPosition,soundId,cardboardAudioEngine);
    this.btnNext.image = this.btnNext.loadTexture(this, R.drawable.button_next2);

    this.btnPrevious.updateModelPosition(this.btnPrevious.modelPosition,soundId,cardboardAudioEngine);
    this.btnPrevious.image = this.btnPrevious.loadTexture(this, R.drawable.button_previous2);

    this.btnZoom.updateModelPosition(this.btnZoom.modelPosition,soundId,cardboardAudioEngine);
    this.btnZoom.image = this.btnZoom.loadTexture(this, R.drawable.button_zoom);

    this.btnUnzoom.updateModelPosition(this.btnUnzoom.modelPosition,soundId,cardboardAudioEngine);
    this.btnUnzoom.image = this.btnUnzoom.loadTexture(this, R.drawable.button_unzoom);

    checkGLError("onSurfaceCreated");
  }

  /**
   * Converts a raw text file into a string.
   *
   * @param resId The resource ID of the raw text file about to be turned into a shader.
   * @return The context of the text file, or null in case of error.
   */
  private String readRawTextFile(int resId) {
    InputStream inputStream = getResources().openRawResource(resId);
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      reader.close();
      return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Prepares OpenGL ES before we draw a frame.
   *
   * @param headTransform The head transformation in the new frame.
   */
  @Override
  public void onNewFrame(HeadTransform headTransform) {
    // Build the Model part of the ModelView matrix.

    // We make the cube rotate
    //Matrix.rotateM(modelCube, 0, TIME_DELTA, 0.5f, 0.5f, 1.0f);

    // Build the camera matrix and apply it to the ModelView.
    Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

    headTransform.getHeadView(headView, 0);

    // Update the 3d audio engine with the most recent head rotation.
    headTransform.getQuaternion(headRotation, 0);
    cardboardAudioEngine.setHeadRotation(
        headRotation[0], headRotation[1], headRotation[2], headRotation[3]);
    // Regular update call to cardboard audio engine. 
    cardboardAudioEngine.update();

    checkGLError("onReadyToDraw");
  }

  /**
   * Draws a frame for an eye.
   *
   * @param eye The eye to render. Includes all required transformations.
   */
  @Override
  public void onDrawEye(Eye eye) {
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    checkGLError("colorParam");

    // Apply the eye transformation to the camera.
    Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

    // Set the position of the light
    Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

    // Build the ModelView and ModelViewProjection matrices
    // for calculating cube position and light.
    float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
    Matrix.multiplyMM(this.table.modelView, 0, view, 0, this.table.model, 0);
    Matrix.multiplyMM(this.table.modelViewProjection, 0, perspective, 0, this.table.modelView, 0);
    this.table.draw(lightPosInEyeSpace,this.table.modelView,this.table.modelViewProjection,headView);
    checkGLError("Drawing cube");

    // Set modelView for the walls, so we draw walls in the correct location
    /*Matrix.multiplyMM(this.murs.modelView, 0, view, 0, this.murs.model, 0);
    Matrix.multiplyMM(this.murs.modelViewProjection, 0, perspective, 0, this.murs.modelView, 0);
    this.murs.draw(lightPosInEyeSpace,this.murs.modelView,this.murs.modelViewProjection,headView);
    checkGLError("Drawing walls");*/

    // Set modelView for the floor, so we draw floor in the correct location
    Matrix.multiplyMM(this.floor.modelView, 0, view, 0, this.floor.model, 0);
    Matrix.multiplyMM(this.floor.modelViewProjection, 0, perspective, 0, this.floor.modelView, 0);
    this.floor.draw(lightPosInEyeSpace,this.floor.modelView,this.floor.modelViewProjection,headView);
    checkGLError("Drawing floor");

    // Set modelView for the sheet, so we draw sheet in the correct location
    Matrix.multiplyMM(this.feuille.modelView, 0, view, 0, this.feuille.model, 0);
    Matrix.multiplyMM(this.feuille.modelViewProjection, 0, perspective, 0, this.feuille.modelView, 0);
    // Load texture
    if(this.pdf.currentPageHasChanged()){
      feuille.image = feuille.loadTexture(this, pdf.render()); //pdf.render(getBaseContext(), feuille, feuille.image);
      this.pdf.hasChanged = false;
    }
    this.feuille.draw(lightPosInEyeSpace,this.feuille.modelView,this.feuille.modelViewProjection,headView);
    checkGLError("Drawing sheet");

    // Set modelView for the sheet, so we draw sheet in the correct location
    Matrix.multiplyMM(this.btnNext.modelView, 0, view, 0, this.btnNext.model, 0);
    Matrix.multiplyMM(this.btnNext.modelViewProjection, 0, perspective, 0, this.btnNext.modelView, 0);
    this.btnNext.draw(lightPosInEyeSpace,this.btnNext.modelView,this.btnNext.modelViewProjection,headView);
    checkGLError("Drawing BtnNext");

    // Set modelView for the sheet, so we draw sheet in the correct location
    Matrix.multiplyMM(this.btnPrevious.modelView, 0, view, 0, this.btnPrevious.model, 0);
    Matrix.multiplyMM(this.btnPrevious.modelViewProjection, 0, perspective, 0, this.btnPrevious.modelView, 0);
    this.btnPrevious.draw(lightPosInEyeSpace,this.btnPrevious.modelView,this.btnPrevious.modelViewProjection,headView);
    checkGLError("Drawing BtnPrevious");

    // Set modelView for the sheet, so we draw sheet in the correct location
    Matrix.multiplyMM(this.btnZoom.modelView, 0, view, 0, this.btnZoom.model, 0);
    Matrix.multiplyMM(this.btnZoom.modelViewProjection, 0, perspective, 0, this.btnZoom.modelView, 0);
    this.btnZoom.draw(lightPosInEyeSpace,this.btnZoom.modelView,this.btnZoom.modelViewProjection,headView);
    checkGLError("Drawing BtnZoom");

    // Set modelView for the sheet, so we draw sheet in the correct location
    Matrix.multiplyMM(this.btnUnzoom.modelView, 0, view, 0, this.btnUnzoom.model, 0);
    Matrix.multiplyMM(this.btnUnzoom.modelViewProjection, 0, perspective, 0, this.btnUnzoom.modelView, 0);
    this.btnUnzoom.draw(lightPosInEyeSpace,this.btnUnzoom.modelView,this.btnUnzoom.modelViewProjection,headView);
    checkGLError("Drawing BtnUnzoom");
  }

  @Override
  public void onFinishFrame(Viewport viewport) {}


  /**
   * Called when the Cardboard trigger is pulled.
   */
  @Override
  public void onCardboardTrigger() {
    Log.i(TAG, "onCardboardTrigger");

    if (this.feuille.isLookingAtObject(headView)) {
      this.overlayView.show3DToast("Looking at Feuille");
      if(this.feuille.isOnTable()){
        this.feuille.isOnTable=false;
        this.btnNext.isOnTable=false;
        this.btnPrevious.isOnTable=false;
      }
      else{
        this.feuille.isOnTable=true;
        this.btnNext.isOnTable=true;
        this.btnPrevious.isOnTable=true;
      }
      this.feuille.updateModelPosition(this.feuille.modelPosition,soundId,cardboardAudioEngine);
      this.btnNext.updateModelPosition(this.btnNext.modelPosition,soundId,cardboardAudioEngine);
      this.btnPrevious.updateModelPosition(this.btnPrevious.modelPosition,soundId,cardboardAudioEngine);
    }
    else if (this.btnNext.isLookingAtObject(headView)) {
      this.overlayView.show3DToast("Looking at BtnNext");
      if(this.pdf.nextPage()) this.pdf.hasChanged=true;
    }
    else if (this.btnPrevious.isLookingAtObject(headView)) {
      this.overlayView.show3DToast("Looking at BtnPrevious");
      if(this.pdf.previousPage()) this.pdf.hasChanged=true;
    }
    else if (this.btnZoom.isLookingAtObject(headView)) {
      this.overlayView.show3DToast("Looking at BtnZoom");
      if((CAMERA_Z-0.1)>0.01) CAMERA_Z-=0.1f;
    }
    else if (this.btnUnzoom.isLookingAtObject(headView)) {
      this.overlayView.show3DToast("Looking at BtnUnzoom");
      if((CAMERA_Z+0.1)<0.51) CAMERA_Z+=0.1f;
    }
    else{
      this.overlayView.show3DToast("Looking at Nothing");
      getCardboardView().resetHeadTracker();
    }

    //this.overlayView.show3DToast("currentPage : " + String.valueOf(this.pdf.currentPage) + " / isOnTable : " + String.valueOf(this.feuille.isOnTable));

    // Always give user feedback.
    vibrator.vibrate(50);
  }

}
