package com.vlvolad.pendulumstudio.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;


public class SpringGL {
	private final FloatBuffer vertexBuffer;
	private final FloatBuffer normalBuffer;
	static final int COORDS_PER_VERTEX = 3;
	private final int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float rad;
    public SpringGL(float aa, int iters) {
        // initialize vertex byte buffer for shape coordinates
    	int iterZ = 1;
    	//rad = Radius;
        vertexCount = iters * 10 * 6;
    	ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
        		iters * 10 * 6 * 3 * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        
        ByteBuffer bbn = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
        		iters * 10 * 6 * 3 * 4);
        // use the device hardware's native byte order
        bbn.order(ByteOrder.nativeOrder());
        normalBuffer = bbn.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        float stz = 0.f;
        float dz = 0.9f/iters;
        vertexBuffer.put(0.f);
        normalBuffer.put(0.f);
        vertexBuffer.put(0.f);
        normalBuffer.put(0.f);
        vertexBuffer.put(0.f);
        normalBuffer.put(0.f);
        
        for(int i=0;i<iters;++i)
        {
            stz = 0.03f + i * dz;
            for(int j=0;j<10;++j)
            {
            	vertexBuffer.put(aa/15.f * (float)Math.cos(2.f*Math.PI*j/10.f));
                normalBuffer.put(0.f);
                vertexBuffer.put(aa/15.f * (float)Math.sin(2.f*Math.PI*j/10.f));
                normalBuffer.put(0.f);
                vertexBuffer.put(stz + j*dz/10.f);
                normalBuffer.put(0.f);
            	
            	vertexBuffer.put(aa/15.f * (float)Math.cos(2.f*Math.PI*(j+1)/10.f));
                normalBuffer.put(0.f);
                vertexBuffer.put(aa/15.f * (float)Math.sin(2.f*Math.PI*(j+1)/10.f));
                normalBuffer.put(0.f);
                vertexBuffer.put(stz + (j+1)*dz/10.f);
                normalBuffer.put(0.f);
            }
        }
        
        vertexBuffer.put(0.f);
        normalBuffer.put(0.f);
        vertexBuffer.put(0.f);
        normalBuffer.put(0.f);
        vertexBuffer.put(1.f);
        normalBuffer.put(1.f);
        
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        normalBuffer.position(0);
    }
    public void draw(int prog, float[] mvpMatrix, float[] mvMatrix)
    {
    	int tHandle = GLES20.glGetUniformLocation(prog, "u_mvpMatrix");
		GLES20.glUniformMatrix4fv(tHandle, 1, false, mvpMatrix, 0);
		tHandle = GLES20.glGetUniformLocation(prog, "u_mvMatrix");
		GLES20.glUniformMatrix4fv(tHandle, 1, false, mvMatrix, 0);
		
		tHandle = GLES20.glGetAttribLocation(prog, "a_position");
		GLES20.glVertexAttribPointer(tHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
		GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount/3);
    }
    
}
