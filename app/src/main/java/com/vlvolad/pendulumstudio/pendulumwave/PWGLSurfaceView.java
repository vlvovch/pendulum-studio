package com.vlvolad.pendulumstudio.pendulumwave;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by Volodymyr on 25.05.2015.
 */
public class PWGLSurfaceView extends GLSurfaceView {

    PWGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;
    private int count;

    public PWGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new PWGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        count = 0;

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public PWGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new PWGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        count = 0;
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        count++;

        if (e.getPointerCount()<2)
        {
            switch (e.getAction()) {
                case MotionEvent.ACTION_UP:
                    PWGLRenderer.mPendulum.moved = false;
                    PWGLRenderer.mPendulum.timeInterval2 = -1;
                    count = 0;
                    break;
                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    requestRender();
            }
        }

        mPreviousX = x;
        mPreviousY = y;
        mScaleDetector.onTouchEvent(e);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = PWGLRenderer.mPendulum.zoomIn;
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.35f, Math.min(mScaleFactor, 6.0f));

            PWGLRenderer.mPendulum.zoomIn = mScaleFactor;

            invalidate();
            return true;
        }
    }

}
