package com.example.ejemplo2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.*;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    /** The refresh rate, in frames per second. */
    private static final int REFRESH_RATE_FPS = 20;

    /** The duration, in milliseconds, of one frame. */
    private static final float FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;

    private final float[] mMVPMatrix;
    private final float[] mProjectionMatrix;
    private final float[] mViewMatrix;
    private final float[] mRotationMatrix;
    private final float[] mFinalMVPMatrix;

    private Squaree mSquare;
    private Player mPlayer;
    private EasyCube mEcube[][];

    private float eyeX = 5.0f, eyeY = -5.0f, eyeZ = 5.0f;
    private float centerX = 0.0f, centerY = 0.0f, centerZ = 0.0f;
    private float upX = 0.0f, upY = 0.0f, upZ = 1.0f;

    private float time = 0;
    public MyGLRenderer() {
        mMVPMatrix = new float[16];
        mProjectionMatrix = new float[16];
        mViewMatrix = new float[16];
        mRotationMatrix = new float[16];
        mFinalMVPMatrix = new float[16];
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        Random randomno = new Random();

        mEcube = new EasyCube[20][20];
        for (int i = 0; i<mEcube.length;i++ ){
            for (int j = 0; j<mEcube[0].length; j++){
                mEcube[i][j] = new EasyCube(0.5f*i-5+0.25f, 0.5f*j-5+0.25f, 0.25f, 0.5f);

                // get next next boolean value
                mEcube[i][j].setAlive(randomno.nextBoolean());

                // initializing nexstate with the current state
                mEcube[i][j].setNextState(mEcube[i][j].isAlive());
            }
        }
//        mEcube[1] = new EasyCube(1.5f, 1.0f, -0.25f, 0.5f);
//        mEcube[2] = new EasyCube(-1.5f, -0.5f, -0.25f, 0.5f);
//        mEcube[3] = new EasyCube(1.5f, -0.5f, -0.25f, 0.5f);
//        mEcube[4] = new EasyCube(-0.25f, 0.25f, -0.25f, 0.5f);

        mSquare = new Squaree();
//        mPlayer = new Player(0, 0, -0.15f, 0.3f);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        float ratio = (float) width / height;

        GLES20.glViewport(0, 0, width, height);
        // This projection matrix is applied to object coordinates in the onDrawFrame() method.

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 100.0f);//original
        //                              ?, ?,left,  right, botton, up, znear, zfar

        // modelView = projection x view
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

    }

    public void updateCubes() {
        int filas = mEcube.length;
        int columnas = mEcube[0].length;
        int neighbours;
        for (int i = 0; i < mEcube.length; i++) {
            for (int j = 0; j < mEcube[0].length; j++) {
                neighbours = 0;

                if (i - 1 > 0) {
                    if (mEcube[i - 1][j].isAlive())
                        neighbours++;
                    if (j - 1 > 0 && mEcube[i - 1][j - 1].isAlive())
                        neighbours++;
                    if (j + 1 < columnas && mEcube[i - 1][j + 1].isAlive())
                        neighbours++;
                }

                if (i + 1 < filas) {
                    if (mEcube[i + 1][j].isAlive())
                        neighbours++;
                    if (j - 1 > 0 && mEcube[i + 1][j - 1].isAlive())
                        neighbours++;
                    if (j + 1 < columnas && mEcube[i + 1][j + 1].isAlive())
                        neighbours++;
                }

                if (j + 1 < columnas && mEcube[i][j + 1].isAlive())
                    neighbours++;
                if (j - 1 > 0 && mEcube[i][j - 1].isAlive())
                    neighbours++;

                if (mEcube[i][j].isAlive())
                {
                    if (neighbours < 2 || neighbours > 3)
                        mEcube[i][j].setNextState(false);
                    else
                        mEcube[i][j].setNextState(true);
                }
                else {
                    if (neighbours == 3)
                        mEcube[i][j].setNextState(true);
                    else
                        mEcube[i][j].setNextState(false);
                }

            }
        }

        for (int i =0; i< mEcube.length; i++){
            for (int j=0; j< mEcube[0].length; j++){
                mEcube[i][j].updateState();
            }
        }

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        //Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, forfront, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);//

        // Apply the rotation.
        Matrix.setRotateM(mRotationMatrix, 0, 0, 1.0f, 1.0f, 1.0f);
        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mFinalMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        for (int i = 0; i < mEcube.length ; i++) {
            for (int j = 0; j < mEcube[0].length ; j++) {
                    if (mEcube[i][j].isAlive())
                        mEcube[i][j].draw(mFinalMVPMatrix);
            }
        }
        Log.d("miau", "time = "+time);
        mSquare.draw(mFinalMVPMatrix);
//        mPlayer.draw(mFinalMVPMatrix);
        if (time > 20){
            time =0;
            updateCubes();
        }
        time++;
    }

    public void updateEye(float dd) {
        float angle = (float)Math.atan((centerY-eyeY) / (centerX - eyeX));
        float dy = centerY - eyeY;
        float dx = centerX - eyeX;

        if(dx < 0.0f){
            angle = angle + (float)Math.PI;
        }

        eyeX = eyeX + dd * (float)Math.cos(angle);
        eyeY = eyeY + dd * (float)Math.sin(angle);

        centerX = centerX + dd * (float)Math.cos(angle);
        centerY = centerY + dd * (float)Math.sin(angle);
    }

    public void rotateCam(float dd) {
        float angle = (float)Math.atan((centerY-eyeY) / (centerX - eyeX));
        float dy = centerY - eyeY;
        float dx = centerX - eyeX;

        if(dx < 0.0f){
            angle = angle + (float)Math.PI;
        }

        float tmpX = eyeX + dx * (float)Math.cos(dd) - dy * (float) Math.sin(dd);
        float tmpY = eyeY + dy * (float)Math.cos(dd) + dx * (float) Math.sin(dd);
        centerX = tmpX;
        centerY = tmpY;
    }

    public float getX() {
        return eyeX;
    }

    public float getY() {
        return eyeY;
    }

    public float getZ() {
        return eyeZ;
    }

//    public void updateEnemigo(float x, float y) {
//        mPlayer = new Player(x, y, -2.0f, 0.5f);
//    }

}
