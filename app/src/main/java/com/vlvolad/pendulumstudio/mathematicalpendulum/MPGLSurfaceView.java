package com.vlvolad.pendulumstudio.mathematicalpendulum;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class MPGLSurfaceView extends GLSurfaceView {

    MPGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mTapDetector;
    private int count;

    public MPGLSurfaceView(Context context) {
        super(context);

        init();
    }
    
    public MPGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		init();
	}

	public void init() {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MPGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        mTapDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                final MPGLActivity act = (MPGLActivity)getContext();
                act.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        if (act.buttonsAreOff)
                            act.timerHandler.post(act.timerButtonsOn);
                        else {
                            act.timerHandler.removeCallbacks(act.timerButtonsOff);
                            act.timerHandler.postDelayed(act.timerButtonsOff, act.buttonsFadeOutTime);
                        }
                    } });
                return true;
            }
        });

        count = 0;

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
        		MPGLRenderer.mPendulum.moved = false;
        		MPGLRenderer.mPendulum.timeInterval2 = -1;
        		count = 0;
        		break;
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                
                if (count>2) MPGLRenderer.mPendulum.setCoord(x, y, dx, dy, mRenderer.Width, mRenderer.Height);

                requestRender();
        }
        }

        mPreviousX = x;
        mPreviousY = y;
    	mScaleDetector.onTouchEvent(e);
        mTapDetector.onTouchEvent(e);
        return true;
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = MPGLRenderer.mPendulum.zoomIn; 
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.35f, Math.min(mScaleFactor, 6.0f));
            
            MPGLRenderer.mPendulum.zoomIn = mScaleFactor;

            invalidate();
            return true;
        }
    }

}
