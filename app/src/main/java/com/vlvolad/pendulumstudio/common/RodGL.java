package com.vlvolad.pendulumstudio.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;


public class RodGL {
	private final FloatBuffer vertexBuffer;
	private final FloatBuffer normalBuffer;
	static final int COORDS_PER_VERTEX = 3;
	private final int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float rad;
    public RodGL(float Length, float zoffset, float Radius, int iterPhi) {
        // initialize vertex byte buffer for shape coordinates
    	int iterZ = 1;
    	rad = Radius;
        vertexCount = iterPhi * iterZ * 6;
    	ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
        		iterPhi * iterZ * 6 * 3 * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        
        ByteBuffer bbn = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
        		iterPhi * iterZ * 6 * 3 * 4);
        // use the device hardware's native byte order
        bbn.order(ByteOrder.nativeOrder());
        normalBuffer = bbn.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        float dphi = (float) (2 * Math.PI / iterPhi);
        //float dtheta = (float) (Math.PI / iterTheta);
        float phi = 0.f, theta = 0.f;
        float x[] = new float[4]; 
        float y[] = new float[4]; 
        float z[] = new float[4];
        float norm[] = new float[4];
        int iters = 0;
        float dZ = (float)(Length/iterZ);
        float cz = 0.f;
        for(int k=0;k<iterZ;++k)
        {
        	cz = zoffset + (float)(k*dZ);
        for(int i=0;i<iterPhi;++i)
        {
            phi = (float)(Math.PI / 2.f + i*dphi);
            {
                x[0] = (float)Math.cos(phi);
                y[0] = (float)Math.sin(phi);
                z[0] = cz;
                x[1] = (float)Math.cos(phi);
                y[1] = (float)Math.sin(phi);
                z[1] = cz + dZ;
                x[2] = (float)Math.cos(phi + dphi);
                y[2] = (float)Math.sin(phi + dphi);
                z[2] = cz + dZ;
                x[3] = (float)Math.cos(phi + dphi);
                y[3] = (float)Math.sin(phi + dphi);
                z[3] = cz;
                
                vertexBuffer.put(rad*x[0]);
                normalBuffer.put(x[0]);
                vertexBuffer.put(rad*y[0]);
                normalBuffer.put(y[0]);
                vertexBuffer.put(z[0]);
                normalBuffer.put(0.f);
                
                vertexBuffer.put(rad*x[1]);
                normalBuffer.put(x[1]);
                vertexBuffer.put(rad*y[1]);
                normalBuffer.put(y[1]);
                vertexBuffer.put(z[1]);
                normalBuffer.put(0.f);
                
                vertexBuffer.put(rad*x[2]);
                normalBuffer.put(x[2]);
                vertexBuffer.put(rad*y[2]);
                normalBuffer.put(y[2]);
                vertexBuffer.put(z[2]);
                normalBuffer.put(0.f);
                
                vertexBuffer.put(rad*x[0]);
                normalBuffer.put(x[0]);
                vertexBuffer.put(rad*y[0]);
                normalBuffer.put(y[0]);
                vertexBuffer.put(z[0]);
                normalBuffer.put(0.f);
                
                vertexBuffer.put(rad*x[2]);
                normalBuffer.put(x[2]);
                vertexBuffer.put(rad*y[2]);
                normalBuffer.put(y[2]);
                vertexBuffer.put(z[2]);
                normalBuffer.put(0.f);
                
                vertexBuffer.put(rad*x[3]);
                normalBuffer.put(x[3]);
                vertexBuffer.put(rad*y[3]);
                normalBuffer.put(y[3]);
                vertexBuffer.put(z[3]);
                normalBuffer.put(0.f);
                
                iters += 6;
            }
        }
        }
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
		tHandle = GLES20.glGetAttribLocation(prog, "a_normal");
		GLES20.glEnableVertexAttribArray(tHandle);
		GLES20.glVertexAttribPointer(tHandle, 3,
                GLES20.GL_FLOAT, false,
                0, normalBuffer);
    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    	GLES20.glDisableVertexAttribArray(tHandle);
    }
    
}
