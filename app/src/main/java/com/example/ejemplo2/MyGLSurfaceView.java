package com.example.ejemplo2;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;
    private float px, py, pz;

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
//      setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                if(x < w/4) {
                    System.out.println("LEFT");
                    mRenderer.rotateCam(0.012f);
                }

                if(w/4 <= x && x < 3*w/4)
                {
                    if(y>h/2) {
                        System.out.println("DOWN");
                        mRenderer.updateEye(-0.07f);
                    }
                    else {
                        System.out.println("UP");
                        mRenderer.updateEye(0.07f);
                    }
                }

                if(3*w/4 <= x) {
                    System.out.println("RIGTH");
                    mRenderer.rotateCam(-0.012f);
                }

                px = mRenderer.getX();
                py = mRenderer.getY();
                pz = mRenderer.getZ();

                System.out.println(" -------------------  X: "+ px + "   Y: " + py + "     Z: " + pz);

                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }



}
