package com.vlvolad.pendulumstudio.doublependulum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;


import com.vlvolad.pendulumstudio.common.GenericPendulum;
import com.vlvolad.pendulumstudio.common.SphereGL;
import com.vlvolad.pendulumstudio.common.RodGL;
import com.vlvolad.pendulumstudio.common.TrajectoryGL;

public class DoublePendulum extends GenericPendulum {
    public volatile boolean moved;
	public volatile int moveIndex;
	double l1, l2, m1, m2, g, ken, pen, pp;
	public volatile double k;
	double qo[];
	boolean trmd;
	SphereGL mSphere, mSphere2;
	RodGL mRod, mRod2;
	private FloatBuffer lineVertexBuffer;
	long timeInterval;
	public volatile long timeInterval2;
	public volatile float zoomIn;
	public volatile float moveX;
	public volatile float moveY;
	int coordSystem;

    final float[] mRotationMatrix = new float[16];
	
	private FloatBuffer lightDir, lightHP, lightAC, lightDC, lightSC;
	private FloatBuffer materialAF, materialDF, materialSF;
	float materialshin;

	public static Random generator = new Random();

	public DoublePendulum(double l1, double l2, double m1, double m2,
			double th1, double th2, double thv1,
			double thv2, double gr, double k,
			boolean trmd, int trLength, boolean firsttime)
	{
		super();
        this.name = "Double Pendulum (2D)";
		this.firsttime = firsttime;
		g = gr;
		this.k = k;
		this.l1 = l1;
		this.l2 = l2;
		this.m1 = m1;
		this.m2 = m2;
		this.trmd = trmd;
		sz = 2;
		q = new double[sz];
		qv = new double[sz];
		qo = new double[sz];
		q[0] = th1;
		qv[0] = thv1;
		qo[0] = q[0];
		q[1] = th2;
		qv[1] = thv2;
		qo[1] = q[1];
		qt = new double[sz];
		qvt = new double[sz];
		a = new double[sz];
		k1 = new double[2 * sz];
		k2 = new double[2 * sz];
		k3 = new double[2 * sz];
		k4 = new double[2 * sz];
		k5 = new double[2 * sz];
		k6 = new double[2 * sz];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mRod = new RodGL((float)(l1-10.*Math.pow(m1, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m1, 1./3.)), 30);
		mRod2 = new RodGL((float)(l2-10.*Math.pow(m1, 1./3.)-10.*Math.pow(m2, 1./3.)), (float)(10.*Math.pow(m1, 1./3.)), (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
		ByteBuffer vbb = ByteBuffer.allocateDirect(9 * 4);
		vbb.order(ByteOrder.nativeOrder());
		lineVertexBuffer = vbb.asFloatBuffer();
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		timeInterval = SystemClock.uptimeMillis();
		zoomIn = 0.80f;
		moveX = 0.f;
		moveY = 0.f;
		dynamicGravity = false;
		gy = gx = 0.f;
		gz = 981.f;
		mTrajectory = new TrajectoryGL(trLength, (float)getx1(), (float)gety1(), (float)getz1(), 1.0f, 0.0f, 0.0f);
		mTrajectory2 = new TrajectoryGL(trLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.0f, 0.0f, 1.0f);
		coordSystem = 0;
		lightDir = fill3DVector(0.f, 0.f, 1.0f);
		lightHP = fill3DVector(0.f, 0.f, 1.0f);
		lightAC = fill4DVector(0.3f, 0.3f, 0.3f, 1.0f);
		lightDC = fill4DVector(1.0f, 1.0f, 1.0f, 1.0f);
		lightSC = fill4DVector(1.0f, 1.0f, 1.0f, 1.0f);
		materialAF = fill4DVector(0.2f, 0.2f, 0.2f, 1.0f);
		materialDF = fill4DVector(0.8f, 0.8f, 0.8f, 1.0f);
		materialSF = fill4DVector(1.0f, 1.0f, 1.0f, 1.0f);
		materialshin = 40.0f;
		moveIndex = 0;
		moved = false;
	}
	
	FloatBuffer fill3DVector(float x, float y, float z)
	{
		FloatBuffer buf;
		ByteBuffer vbb = ByteBuffer.allocateDirect(3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        buf = vbb.asFloatBuffer();
        buf.put(x);
        buf.put(y);
        buf.put(z);
        return buf;
	}
	
	FloatBuffer fill4DVector(float x, float y, float z, float w)
	{
		FloatBuffer buf;
		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 4);
        vbb.order(ByteOrder.nativeOrder());
        buf = vbb.asFloatBuffer();
        buf.put(x);
        buf.put(y);
        buf.put(z);
        buf.put(w);
        return buf;
	}

	public void restart() {
        frames = 0;
        fps = 0.f;
		firsttime = false;
		g = DPSimulationParameters.simParams.g;
		this.k = DPSimulationParameters.simParams.k;
		this.l1 = DPSimulationParameters.simParams.l1 * 100.;
		this.l2 = DPSimulationParameters.simParams.l2 * 100.;
		this.m1 = DPSimulationParameters.simParams.m1;
		this.m2 = DPSimulationParameters.simParams.m2;
		this.trmd = DPSimulationParameters.simParams.showTrajectory;
		if (DPSimulationParameters.simParams.initRandom) {
			q[0] = (float) (2. * Math.PI * generator.nextDouble()); // th
			qv[0] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
			q[1] = (float) (2. * Math.PI * generator.nextDouble()); // th
			qv[1] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv

		} else {
			q[0] = DPSimulationParameters.simParams.th1;
			qv[0] = DPSimulationParameters.simParams.thv1;
			q[1] = DPSimulationParameters.simParams.th2;
			qv[1] = DPSimulationParameters.simParams.thv2;
		}
		coordSystem = 0;
		qo[0] = q[0];
		qo[1] = q[1];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mTrajectory = new TrajectoryGL(DPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1(), 1.0f, 0.0f, 0.0f);
		mTrajectory2 = new TrajectoryGL(DPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.0f, 0.0f, 1.0f);
		mRod = new RodGL((float)(l1-10.*Math.pow(m1, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m1, 1./3.)), 30);
		mRod2 = new RodGL((float)(l2-10.*Math.pow(m1, 1./3.)-10.*Math.pow(m2, 1./3.)), (float)(10.*Math.pow(m1, 1./3.)), (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
		timeInterval = SystemClock.uptimeMillis();
		moveIndex = 0;
        setColorPendulum1(DPSimulationParameters.simParams.pendulumColor);
        setColorPendulum2(DPSimulationParameters.simParams.pendulumColor2);
	}

	void restartRandom(double l, double m, double gr, double k, boolean trmd,
			int trLength) {
        frames = 0;
        fps = 0.f;
		g = gr;
		this.k = DPSimulationParameters.simParams.k;
		this.l1 = DPSimulationParameters.simParams.l1;
		this.l2 = DPSimulationParameters.simParams.l2;
		this.m1 = DPSimulationParameters.simParams.m1;
		this.m2 = DPSimulationParameters.simParams.m2;
		this.trmd = trmd;
		coordSystem = 0;
		q[0] = (float) (Math.PI / 1.25 * generator.nextDouble()); // th
		qv[0] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
		q[1] = (float) (Math.PI / 1.25 * generator.nextDouble()); // th
		qv[1] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
		qo[0] = q[0];
		qo[1] = q[1];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mTrajectory = new TrajectoryGL(DPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(DPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
		timeInterval = SystemClock.uptimeMillis();
		coordSystem = 0;
		moveIndex = 0;
	}

	protected void accel(double a[], double qt[], double qvt[]) {
		if (!dynamicGravity)
			accel1(a, qt, qvt);
		else
			accel2(a, qt, qvt);
	}

	void accel1(double a[], double qt[], double qvt[]) {
		double a1,b1,c1,a2,b2,c2;
		a1 = (m1 + m2)*l1;
		b1 = m2*l2*Math.cos(qt[0] - qt[1]);
		c1 = m2*l2*qvt[1]*qvt[1]*Math.sin(qt[0] - qt[1]) + (m1 + m2)*g*Math.sin(qt[0])
				+ 2.*k*l1*qvt[0] + k*l2*qvt[1]*Math.cos(qt[0]-qt[1]);
		a2 = m2*l1*Math.cos(qt[0] - qt[1]);
		b2 = m2*l2;
		c2 = -m2*l1*qvt[0]*qvt[0]*Math.sin(qt[0] - qt[1]) + m2*g*Math.sin(qt[1])
				+ k*l2*qvt[1] + k*l1*qvt[0]*Math.cos(qt[0]-qt[1]);
		a[0] = - ( c2/b2 - c1/b1 ) / ( a2/b2 - a1/b1 );
		a[1] = - ( c2/a2 - c1/a1 ) / ( b2/a2 - b1/a1 );
	}

	void accel2(double a[], double qt[], double qvt[]) {
		double a1,b1,c1,a2,b2,c2;
		a1 = (m1 + m2)*l1;
		b1 = m2*l2*Math.cos(qt[0] - qt[1]);
		c1 = m2*l2*qvt[1]*qvt[1]*Math.sin(qt[0] - qt[1]) + (m1 + m2)*gz*Math.sin(qt[0])
				- (m1 + m2)*gy*Math.cos(qt[0])
				+ 2.*k*l1*qvt[0] + k*l2*qvt[1]*Math.cos(qt[0]-qt[1]);
		a2 = m2*l1*Math.cos(qt[0] - qt[1]);
		b2 = m2*l2;
		c2 = -m2*l1*qvt[0]*qvt[0]*Math.sin(qt[0] - qt[1]) + m2*gz*Math.sin(qt[1])
				- m2*gy*Math.cos(qt[1])
				+ k*l2*qvt[1] + k*l1*qvt[0]*Math.cos(qt[0]-qt[1]);
		a[0] = - ( c2/b2 - c1/b1 ) / ( a2/b2 - a1/b1 );
		a[1] = - ( c2/a2 - c1/a1 ) / ( b2/a2 - b1/a1 );
	}
	
	protected void accelSingle(double a[], double qt[], double qvt[], int index) {
		if (!dynamicGravity)
			accelSingle1(a, qt, qvt, index);
		else
			accelSingle2(a, qt, qvt, index);
	}

	void accelSingle1(double a[], double qt[], double qvt[], int index) {
		if (index==0) {
			a[0] = -g * Math.sin(qt[0]) / l1 - k*qvt[0]/m1;
			a[1] = 0.;
		}
		else {
			a[1] = -g * Math.sin(qt[1]) / l2 - k*qvt[0]/m2;
			a[0] = 0.;
		}
	}

	void accelSingle2(double a[], double qt[], double qvt[], int index) {
		if (index==0) {
			a[0] = -gz * Math.sin(qt[0]) / l1 + gy * Math.cos(qt[0]) / l1;
			a[1] = 0.;
		}
		else {
			a[1] = -gz * Math.sin(qt[1]) / l2 + gy * Math.cos(qt[1]) / l2;
			a[0] = 0.;
		}
	}


	double kien() {
		return (0.5*(m1 + m2)*l1*l1*qv[0]*qv[0] + 0.5*m2*l2*l2*qv[1]*qv[1] + m2*l1*l2*qv[0]*qv[1]*
				Math.cos(q[0]-q[1]))/10000.0;
	}

	double poen() {
		return (- (m1 + m2)*g*l1*Math.cos(q[0]) - m2*g*l2*Math.cos(q[1]))/10000.0;
	}

	double ener() {
		return kien() + poen();
	}

	double getx1()
    {
        return 0.;
    }

    double getxv1()
    {
    	return 0.;
    }

    double getxo1()
    {
    	return 0.;
    }

    double getx2()
    {
    	return 0.;
    }

    double getxv2()
    {
    	return 0.;
    }

    double getxo2()
    {
    	return 0.;
    }

    double gety1()
    {
        return l1 * Math.sin(q[0]);
    }

    double getyv1()
    {
    	return l1 * Math.cos(q[0]) * qv[0];
    }

    double getyo1()
    {
    	return l1 * Math.sin(qo[0]);
    }

    double gety2()
    {
    	return l1 * Math.sin(q[0]) + l2 * Math.sin(q[1]);
    }

    double getyv2()
    {
    	return l1 * Math.cos(q[0]) * qv[0] + l2 * Math.cos(q[1]) * qv[1];
    }

    double getyo2()
    {
    	return l1 * Math.sin(qo[0]) + l2 * Math.sin(qo[1]);
    }

    double getz1()
    {
    	return l1 * Math.cos(q[0]);
    }

    double getzv1()
    {
    	return -l1 * Math.sin(q[0]) * qv[0];
    }

    double getzo1()
    {
    	return l1 * Math.cos(qo[0]);
    }

    double getz2()
    {
    	return l1 * Math.cos(q[0]) + l2 * Math.cos(q[1]);
    }

    double getzv2()
    {
    	return -l1 * Math.sin(q[0]) * qv[0] - l2 * Math.sin(q[1]) * qv[1];
    }

    double getzo2()
    {
    	return l1 * Math.cos(qo[0]) + l2 * Math.cos(qo[1]);
    }

    protected void integrate(double dt, int pre) {
        qo[0] = q[0];
		qo[1] = q[1];

		super.integrate(dt, pre);

		mTrajectory.addPointToTrajectory((float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2.addPointToTrajectory((float)getx2(), (float)gety2(), (float)getz2());
	}
    
    public void SetPendulumIndex(float coordX, float coordY, int Width, int Height) {
    	float x = coordX - Width/2.f;
    	float y = coordY - Height/2.f;
    	float factor = Height / 450.f * zoomIn;
    	x = x / factor;
    	y = y / factor;
    	double d1 = (x-gety1())*(x-gety1()) + (y-getz1())*(y-getz1());
    	double d2 = (x-gety2())*(x-gety2()) + (y-getz2())*(y-getz2());
    	if (d1<d2) moveIndex = 1;
    	else moveIndex = 2;
    }
    
    public void setCoord(float coordX, float coordY, float dx, float dy, int Width, int Height) {
    	if (moveIndex==0) return;
    	float x = coordX - Width/2.f;
    	float y = coordY - Height/2.f;
    	float factor = Height / 450.f * zoomIn;
    	qo[0] = q[0];
    	qo[1] = q[1];
    	x = x / factor;
    	y = y / factor;
    	long elap = SystemClock.uptimeMillis() - timeInterval2;
    	if (timeInterval2<0) elap = 100000000;
    	float xv = dx / factor / elap * 1000.f;
    	float yv = dy / factor / elap * 1000.f;
    	
    	double x1, x2, y1, y2;
    	double xv1, xv2, yv1, yv2;
    	
    	if (moveIndex==1) {
    		x2 = gety2();
    		y2 = getz2();
    		xv2 = getyv2();
    		yv2 = getzv2();
    		
    		x1 = x;
    		y1 = y;
    		xv1 = xv;
    		yv1 = yv;
    	}
    	else {
    		x2 = x;
    		y2 = y;
    		xv2 = xv;
    		yv2 = yv;
    		
    		x1 = gety1();
    		y1 = getz1();
    		xv1 = getyv1();
    		yv1 = getzv1();
    	}
    	
    	x2 = x2 - x1;
    	xv2 = xv2 - xv1;
    	y2 = y2 - y1;
    	yv2 = yv2 - yv1;
    	
    	l1 = Math.sqrt(x1*x1+y1*y1);
    	l2 = Math.sqrt(x2*x2+y2*y2);

    	q[0] = Math.atan2(x1, y1);

    	q[1] = Math.atan2(x2, y2);

		timeInterval2 = SystemClock.uptimeMillis();
    	moved = true;
    	mTrajectory.addPointToTrajectory((float)getx1(), (float)gety1(), (float)getz1());
    	mTrajectory2.addPointToTrajectory((float)getx2(), (float)gety2(), (float)getz2());
    	mRod = new RodGL((float)(l1-10.*Math.pow(m1, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m1, 1./3.)), 30);
    	mRod2 = new RodGL((float)(l2-10.*Math.pow(m2, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
    }

	public void translateMVMatrix(float[] MVMatrix, int Width, int Height) {
		Matrix.setIdentityM(MVMatrix, 0);
		float factor = Height / 450.f;
		Matrix.translateM(MVMatrix, 0, Width / 2.f, Height / 2.f, 0.f);
		Matrix.scaleM(MVMatrix, 0, factor * zoomIn, factor * zoomIn, factor * zoomIn);
		Matrix.translateM(MVMatrix, 0, 0.f, 30.f * Height / 3000.f, 0.f);
		Matrix.translateM(MVMatrix, 0, moveX, moveY, 0.f);
		Matrix.rotateM(MVMatrix, 0, 90.f, 1.0f, 0.0f, 0.0f);
		Matrix.rotateM(MVMatrix, 0, -90.f, 0.0f, 0.0f, 1.0f);
	}

	public void preDraw() {
		if (firsttime) {
			restart();
		}
		long elap = SystemClock.uptimeMillis() - timeInterval;
		timeInterval = SystemClock.uptimeMillis();
		if (elap < 0 || elap > 50) elap = 1;
		if (!moved && !paused) integrate(elap / 1000., 10);
	}

	public void draw(GL10 unused, int Width, int Height) {
//		DPGLRenderer.perspectiveGL(mProjMatrix, 45.0f,(float)(Width)/Height,0.1f,1200.0f);
		DPGLRenderer.orthoGL(mProjMatrix, Width, Height);
	    Matrix.setIdentityM(mVMatrix, 0);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		float factor = Height / 450.f;
		
		Matrix.translateM(mVMatrix, 0, Width/2.f, Height/2.f, 0.f);
		Matrix.scaleM(mVMatrix, 0, factor*zoomIn,  factor*zoomIn, factor*zoomIn);
		Matrix.translateM(mVMatrix, 0, 0.f, 30.f*Height/3000.f, 0.f);
	    Matrix.translateM(mVMatrix, 0, moveX, moveY, 0.f);
	    Matrix.rotateM(mVMatrix, 0, 90.f, 1.0f, 0.0f, 0.0f);
	    Matrix.rotateM(mVMatrix, 0, -90.f, 0.0f, 0.0f, 1.0f);

		float x, y, z;
		x = (float) getx1();// (float) (l*Math.sin(q[0])*Math.cos(q[1]));
		y = (float) gety1();// (float) (l*Math.sin(q[0])*Math.sin(q[1]));
		z = (float) getz1();// (float) (l*Math.cos(q[0]));
		lineVertexBuffer.put(3, x);
		lineVertexBuffer.put(4, y);
		lineVertexBuffer.put(5, z);
		lineVertexBuffer.position(0);
		
		int tHandle = GLES20.glGetUniformLocation(mProgram, "color");
        GLES20.glUniform4f(tHandle, Color1R/255.f, Color1G/255.f, Color1B/255.f, 1.0f);
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 0);
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_mvpMatrix");
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		GLES20.glUniformMatrix4fv(tHandle, 1, false, mMVPMatrix, 0);
		int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, lineVertexBuffer);

		if (DPSimulationParameters.simParams.showTrajectory && !DPSimulationParameters.simParams.infiniteTrajectory)
			mTrajectory.draw(mProgram, mMVPMatrix);

		float x2, y2, z2;
		x2 = (float) getx2();
		y2 = (float) gety2();
		z2 = (float) getz2();
		
		lineVertexBuffer.put(6, x2);
		lineVertexBuffer.put(7, y2);
		lineVertexBuffer.put(8, z2);
		lineVertexBuffer.position(3);

		tHandle = GLES20.glGetUniformLocation(mProgram, "color");
        GLES20.glUniform4f(tHandle, Color2R/255.f, Color2G/255.f, Color2B/255.f, 1.0f);
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, lineVertexBuffer);

		if (DPSimulationParameters.simParams.showTrajectory && !DPSimulationParameters.simParams.infiniteTrajectory)
			mTrajectory2.draw(mProgram, mMVPMatrix);
		
		
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 1);
		
		tHandle = GLES20.glGetUniformLocation(mProgram, "color");
        GLES20.glUniform4f(tHandle, Color1R/255.f, Color1G/255.f, Color1B/255.f, 1.0f);
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_directionalLight.direction");
		lightDir.position(0);
		GLES20.glUniform3fv(tHandle, 1, lightDir);
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_directionalLight.halfplane");
		lightHP.position(0);
		GLES20.glUniform3fv(tHandle, 1, lightHP);
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_directionalLight.ambientColor");
		lightAC.position(0);
		GLES20.glUniform4fv(tHandle, 1, lightAC);
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_directionalLight.diffuseColor");
		lightDC.position(0);
		GLES20.glUniform4fv(tHandle, 1, lightDC);
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_directionalLight.specularColor");
		lightSC.position(0);
		GLES20.glUniform4fv(tHandle, 1, lightSC);
		
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_material.shininess");
		GLES20.glUniform1f(tHandle, materialshin);
		tHandle = GLES20.glGetUniformLocation(mProgram, "u_material.specularFactor");
		materialSF.position(0);
		GLES20.glUniform4fv(tHandle, 1, materialSF);
		
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 0);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.atan2(y, x)), 0.f, 0.f, 1.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.acos(z/(float)Math.sqrt(x*x+y*y+z*z))), 0.f, 1.f, 0.f);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mRod.draw(mProgram, mMVPMatrix, mVMatrix);
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 1);
		
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.acos(z/(float)Math.sqrt(x*x+y*y+z*z))), 0.f, -1.f, 0.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.atan2(y, x)), 0.f, 0.f, -1.f);
		
		Matrix.translateM(mVMatrix, 0, x, y, z);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mSphere.draw(mProgram, mMVPMatrix, mVMatrix);
		
		tHandle = GLES20.glGetUniformLocation(mProgram, "color");
        GLES20.glUniform4f(tHandle, Color2R/255.f, Color2G/255.f, Color2B/255.f, 1.0f);
		
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 0);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.atan2(y2-y, x2-x)), 0.f, 0.f, 1.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.acos((z2-z)/(float)Math.sqrt((x2-x)*(x2-x)+(y2-y)*(y2-y)+(z2-z)*(z2-z)))), 0.f, 1.f, 0.f);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mRod2.draw(mProgram, mMVPMatrix, mVMatrix);
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 1);
		
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.acos((z2-z)/(float)Math.sqrt((x2-x)*(x2-x)+(y2-y)*(y2-y)+(z2-z)*(z2-z)))), 0.f, -1.f, 0.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.atan2(y2-y, x2-x)), 0.f, 0.f, -1.f);
		
		Matrix.translateM(mVMatrix, 0, x2-x, y2-y, z2-z);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
		mSphere2.draw(mProgram, mMVPMatrix, mVMatrix);
		
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 0);
	    GLES20.glDisable(GLES20.GL_DEPTH_TEST);

	}

	public void toggleGravity() {
		dynamicGravity = !dynamicGravity;
	}

    public void setGravity(float ggx, float ggy, float ggz) {
		gx = 100.f * (ggz);
		gy = 100.f * (-ggx);
		gz = 100.f * (ggy);
	}
	
	protected void integrateSingle(double dt, int pre, int index)
	{
		t = 0;
		hmin = dt/pre;
		//hmax = max(dt/pre,dt/10.0);
		hmax = dt;
		if (h>hmax) h = hmax;
		if (h<hmin) h = hmin;
		
		while (t<dt)
		{
			if (t+h>dt) h=dt-t;
			for(int i=0;i<sz;++i) 
			{
				qt[i] = q[i]; 
				qvt[i] = qv[i];
			}	
			accelSingle(a,qt,qvt,index);
			for(int i=0;i<sz;++i) 
			{
				k1[i] = h*qvt[i]; 
				k1[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
				if (i==index)
				{
					qt[i] = q[i]+b2*k1[i];
					qvt[i] = qv[i]+b2*k1[sz+i];
				}
			accelSingle(a,qt,qvt,index);
			for(int i=0;i<sz;++i) 
			{
				k2[i] = h*qvt[i];
				k2[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
				if (i==index)
				{
					qt[i] = q[i]+b3*k1[i]+c3*k2[i];
					qvt[i] = qv[i]+b3*k1[sz+i]+c3*k2[sz+i];
				}
			accelSingle(a,qt,qvt,index);
			for(int i=0;i<sz;++i) 
			{
				k3[i] = h*qvt[i];
				k3[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
				if (i==index)
				{
					qt[i] = q[i]+b4*k1[i]+c4*k2[i]+d4*k3[i]; 
					qvt[i] = qv[i]+b4*k1[sz+i]+c4*k2[sz+i]+d4*k3[sz+i];
				}
			accelSingle(a,qt,qvt,index);
			for(int i=0;i<sz;++i) 
			{
				k4[i] = h*qvt[i];
				k4[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
				if (i==index)
				{
					qt[i] = q[i]+b5*k1[i]+c5*k2[i]+d5*k3[i]+e5*k4[i];
					qvt[i] = qv[i]+b5*k1[sz+i]+c5*k2[sz+i]+d5*k3[sz+i]+e5*k4[sz+i];
				}
			accelSingle(a,qt,qvt,index);
			for(int i=0;i<sz;++i) 
			{
				k5[i] = h*qvt[i];
				k5[sz + i] = h*a[i];
			}

			for(int i=0;i<sz;++i) 
				if (i==index)
				{
					qt[i] = q[i]+b6*k1[i]+c6*k2[i]+d6*k3[i]+e6*k4[i]+f6*k5[i];
					qvt[i] = qv[i]+b6*k1[sz+i]+c6*k2[sz+i]+d6*k3[sz+i]+e6*k4[sz+i]+f6*k5[sz+i];
				}
			accelSingle(a,qt,qvt,index);
			for(int i=0;i<sz;++i) 
			{
				k6[i] = h*qvt[i];
				k6[sz + i] = h*a[i];
			}

	        err = Math.abs(r1*k1[0]+r3*k3[0]+r4*k4[0]+r5*k5[0]+r6*k6[0]);
			for(int i=0;i<2*sz-1;++i)
	            if (err<Math.abs(r1*k1[i+1]+r3*k3[i+1]+r4*k4[i+1]+r5*k5[i+1]+r6*k6[i+1]))
	                err = Math.abs(r1*k1[i+1]+r3*k3[i+1]+r4*k4[i+1]+r5*k5[i+1]+r6*k6[i+1]);
			if (err<eps || h<2*hmin)
			{
				for(int i=0;i<sz;++i)
					if (i==index)
					{
						q[i] += n1*k1[i]+n3*k3[i]+n4*k4[i]+n5*k5[i];
						qv[i] += n1*k1[sz+i]+n3*k3[sz+i]+n4*k4[sz+i]+n5*k5[sz+i];
					}
				t += h;
			}
			s = Math.sqrt( Math.sqrt ( eps*h/2/err) );
			h *= s;
	        if (h>hmax) h = hmax;
	        if (h<hmin) h = hmin;
		}
	}

    public void clearTrajectory() {
        mTrajectory.clearTrajectory((float)getx1(), (float)gety1(), (float)getz1());
        mTrajectory2.clearTrajectory((float)getx2(), (float)gety2(), (float)getz2());
    }

    public void setDampingMode(boolean enabled) {
        if (enabled) this.k = DPSimulationParameters.simParams.k;
        else this.k = 0.;
    }

    public void setTraceMode(boolean enabled) {
        DPSimulationParameters.simParams.showTrajectory = enabled;
    }
}
