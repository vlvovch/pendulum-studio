package com.vlvolad.pendulumstudio.sphericalpendulum;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class SPGLSurfaceView extends GLSurfaceView {

    SPGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;

    public SPGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SPGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    public SPGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		// Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SPGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
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

        mPreviousX = x;
        mPreviousY = y;
    	mScaleDetector.onTouchEvent(e);
        return true;
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = SPGLRenderer.mPendulum.zoomIn; 
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.35f, Math.min(mScaleFactor, 6.0f));
            
            SPGLRenderer.mPendulum.zoomIn = mScaleFactor;


            invalidate();
            return true;
        }
    }

}
