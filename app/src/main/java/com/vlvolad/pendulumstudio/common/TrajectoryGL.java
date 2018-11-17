package com.vlvolad.pendulumstudio.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class TrajectoryGL {
	private FloatBuffer vertexBuffer, colorBuffer;
	static final int COORDS_PER_VERTEX = 3;
	private int st, fin;
	public int trajectoryPoints;
    public int mProgram;
	public float mColorRed, mColorBlue, mColorGreen;

    public TrajectoryGL(int pointsNumber, float x, float y, float z) {
        trajectoryPoints = pointsNumber;
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                2 * trajectoryPoints * 3 * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();

        vertexBuffer.put(0, x);// (float)
        // (l*Math.cos(q[1])*Math.sin(q[0])));
        vertexBuffer.put(1, y);// (float)
        // (l*Math.sin(q[1])*Math.sin(q[0])));
        vertexBuffer.put(2, z);// (float) (l*Math.cos(q[0])));

        st = 0;
        fin = 1;

        ByteBuffer bbc = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                trajectoryPoints * 4 * 4);
        // use the device hardware's native byte order
        bbc.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        colorBuffer = bbc.asFloatBuffer();

        for(int i = 0; i<trajectoryPoints; ++i) {
            colorBuffer.put(4*i + 0, 1.0f);
            colorBuffer.put(4*i + 1, 0.0f);
            colorBuffer.put(4*i + 2, 0.0f);
            colorBuffer.put(4*i + 3, i/(float)(trajectoryPoints-1));
        }

        colorBuffer.position(0);

		mColorRed    = 1.0f;
		mColorGreen  = 0.0f;
		mColorBlue   = 0.0f;
    }

	public TrajectoryGL(int pointsNumber, float x, float y, float z, float r, float g, float b) {
		trajectoryPoints = pointsNumber;
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (number of coordinate values * 4 bytes per float)
				2 * trajectoryPoints * 3 * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();
		
		vertexBuffer.put(0, x);// (float)
										// (l*Math.cos(q[1])*Math.sin(q[0])));
		vertexBuffer.put(1, y);// (float)
										// (l*Math.sin(q[1])*Math.sin(q[0])));
		vertexBuffer.put(2, z);// (float) (l*Math.cos(q[0])));
			
		st = 0;
		fin = 1;

        ByteBuffer bbc = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                trajectoryPoints * 4 * 4);
        // use the device hardware's native byte order
        bbc.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        colorBuffer = bbc.asFloatBuffer();

        for(int i = 0; i<trajectoryPoints; ++i) {
            colorBuffer.put(4*i + 0, r);
            colorBuffer.put(4*i + 1, g);
            colorBuffer.put(4*i + 2, b);
            colorBuffer.put(4*i + 3, i/(float)(trajectoryPoints-1));
        }

        colorBuffer.position(4 * (trajectoryPoints - (fin - st)));

		mColorRed    = r;
		mColorGreen  = g;
		mColorBlue   = b;
	}

	public void clearTrajectory(float x, float y, float z) {
		st = 0;
		fin = 1;
		vertexBuffer.put(0, x);// (float)

		vertexBuffer.put(1, y);// (float)

		vertexBuffer.put(2, z);// (float) (l*Math.cos(q[0])));

        colorBuffer.position(4 * (trajectoryPoints - (fin - st)));
	}

	public void addPointToTrajectory(float x, float y, float z) {
		if (fin >= 2 * trajectoryPoints) {
			float arr[] = new float[3 * (fin - st - 1)];
			vertexBuffer.position(3 * (st + 1));
			for (int i = 0; i < (fin - st - 1) * 3; ++i)
				arr[i] = vertexBuffer.get();
			vertexBuffer.position(0);
			vertexBuffer.put(arr, 0, (fin - st - 1) * 3);

			st = 0;
			fin = trajectoryPoints - 1;
			vertexBuffer.put(3 * fin + 0, x);// (float)

			vertexBuffer.put(3 * fin + 1, y);// (float)

			vertexBuffer.put(3 * fin + 2, z);// (float)

			fin++;
		} else {
			if (fin - st >= trajectoryPoints)
				st++;
			/*
			 * traj[3*st2] = (float)q[0]; traj[3*st2+1] = (float)q[1];
			 * traj[3*st2+2] = (float)q[2];
			 */
			vertexBuffer.put(3 * fin + 0, x);// (float)

			vertexBuffer.put(3 * fin + 1, y);// (float)

			vertexBuffer.put(3 * fin + 2, z);// (float)

			fin++;
		}
        colorBuffer.position(4 * (trajectoryPoints - (fin - st)));
	}

    public void setColor(float r, float g, float b) {
        for(int i = 0; i<trajectoryPoints; ++i) {
            colorBuffer.put(4*i + 0, r);
            colorBuffer.put(4*i + 1, g);
            colorBuffer.put(4*i + 2, b);
            colorBuffer.put(4*i + 3, i/(float)(trajectoryPoints-1));
        }
    }


	public void draw(int prog, float[] mvpMatrix) {
        mProgram = prog;
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		vertexBuffer.position(3 * st);
		int tHandle = GLES20.glGetAttribLocation(mProgram, "u_mvpMatrix");
		GLES20.glUniformMatrix4fv(tHandle, 1, false, mvpMatrix, 0);
		tHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
		GLES20.glVertexAttribPointer(tHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        tHandle = GLES20.glGetUniformLocation(mProgram, "trajectory");
        GLES20.glUniform1i(tHandle, 1);
        tHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(tHandle);
        GLES20.glVertexAttribPointer(tHandle, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

    	GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, fin - st);
        GLES20.glDisableVertexAttribArray(tHandle);
        tHandle = GLES20.glGetUniformLocation(mProgram, "trajectory");
        GLES20.glUniform1i(tHandle, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}

	public void drawNext(int prog, float[] mvpMatrix) {
		if (fin - st > 1) {
			mProgram = prog;
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			vertexBuffer.position(3 * (fin - 2));
			colorBuffer.position(4 * (trajectoryPoints - 2));

			int tHandle = GLES20.glGetAttribLocation(mProgram, "u_mvpMatrix");
			GLES20.glUniformMatrix4fv(tHandle, 1, false, mvpMatrix, 0);
			tHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
			GLES20.glEnableVertexAttribArray(tHandle);
			GLES20.glVertexAttribPointer(tHandle, 3,
					GLES20.GL_FLOAT, false,
					0, vertexBuffer);
			tHandle = GLES20.glGetUniformLocation(mProgram, "trajectory");
			GLES20.glUniform1i(tHandle, 0);
			tHandle = GLES20.glGetUniformLocation(mProgram, "color");
			GLES20.glUniform4f(tHandle, mColorRed, mColorGreen, mColorBlue, 1.0f);
			GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 2);
			tHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
			GLES20.glDisableVertexAttribArray(tHandle);
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		}
	}
}
