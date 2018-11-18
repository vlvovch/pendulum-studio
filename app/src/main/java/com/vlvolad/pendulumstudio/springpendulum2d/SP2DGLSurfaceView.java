package com.vlvolad.pendulumstudio.springpendulum2d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class SP2DGLSurfaceView extends GLSurfaceView {

	SP2DGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mTapDetector;
    private int count;

    public SP2DGLSurfaceView(Context context) {
        super(context);

        init();
    }
    
    public SP2DGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		init();
	}

    public void init() {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SP2DGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mTapDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                final SP2DGLActivity act = (SP2DGLActivity)getContext();
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
        		SP2DGLRenderer.mPendulum.moved = false;
        		SP2DGLRenderer.mPendulum.timeInterval2 = -1;
        		count = 0;
        		break;
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                
                if (count>2) SP2DGLRenderer.mPendulum.setCoord(x, y, dx, dy, mRenderer.Width, mRenderer.Height);

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
        	count = 0;
            float mScaleFactor = SP2DGLRenderer.mPendulum.zoomIn; 
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.35f, Math.min(mScaleFactor, 6.0f));
            
            SP2DGLRenderer.mPendulum.zoomIn = mScaleFactor;

            invalidate();
            return true;
        }
    }

}
