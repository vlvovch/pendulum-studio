package com.vlvolad.pendulumstudio.springmathematicalpendulum;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class SMPGLSurfaceView extends GLSurfaceView {

	SMPGLRenderer mRenderer;
    private ScaleGestureDetector mScaleDetector;
    private int count;

    public SMPGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SMPGLRenderer();
        setRenderer(mRenderer);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        
        count = 0;

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    public SMPGLSurfaceView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		// Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SMPGLRenderer();
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
        
        

        if (e.getPointerCount()<2)
        {
        switch (e.getAction()) {
        	case MotionEvent.ACTION_UP:
        		SMPGLRenderer.mPendulum.moved = false;
        		SMPGLRenderer.mPendulum.moveIndex = 0;
        		SMPGLRenderer.mPendulum.timeInterval2 = -1;
        		count = 0;
        		break;
            case MotionEvent.ACTION_MOVE:
            	
            	count++;

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                
                if (count<=1) SMPGLRenderer.mPendulum.SetPendulumIndex(x, y, mRenderer.Width, mRenderer.Height);
                
                if (count>2) SMPGLRenderer.mPendulum.setCoord(x, y, dx, dy, mRenderer.Width, mRenderer.Height);

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
            float mScaleFactor = SMPGLRenderer.mPendulum.zoomIn; 
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.35f, Math.min(mScaleFactor, 6.0f));
            
            SMPGLRenderer.mPendulum.zoomIn = mScaleFactor;

            invalidate();
            return true;
        }
    }

}
