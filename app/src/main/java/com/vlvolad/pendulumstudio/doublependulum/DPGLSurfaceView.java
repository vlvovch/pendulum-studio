package com.vlvolad.pendulumstudio.doublependulum;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class DPGLSurfaceView extends GLSurfaceView {

    DPGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mTapDetector;
    private int count;

    public DPGLSurfaceView(Context context) {
        super(context);

        init();
    }
    
    public DPGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		init();
	}

	public void init() {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new DPGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        mTapDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                final DPGLActivity act = (DPGLActivity)getContext();
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
        
        

        if (e.getPointerCount()<2)
        {
        switch (e.getAction()) {
        	case MotionEvent.ACTION_UP:
        		DPGLRenderer.mPendulum.moved = false;
        		DPGLRenderer.mPendulum.moveIndex = 0;
        		DPGLRenderer.mPendulum.timeInterval2 = -1;
        		count = 0;
        		break;
            case MotionEvent.ACTION_MOVE:
            	
            	count++;

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                
                if (count<=1) DPGLRenderer.mPendulum.SetPendulumIndex(x, y, mRenderer.Width, mRenderer.Height);
                
                if (count>2) DPGLRenderer.mPendulum.setCoord(x, y, dx, dy, mRenderer.Width, mRenderer.Height);

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
            float mScaleFactor = DPGLRenderer.mPendulum.zoomIn; 
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.35f, Math.min(mScaleFactor, 6.0f));
            
            DPGLRenderer.mPendulum.zoomIn = mScaleFactor;

            invalidate();
            return true;
        }
    }
}
