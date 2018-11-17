package com.vlvolad.pendulumstudio.springmathematicalpendulum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;


import com.vlvolad.pendulumstudio.common.GenericPendulum;
import com.vlvolad.pendulumstudio.common.LESolver;
import com.vlvolad.pendulumstudio.common.MechSystemrkf45;
import com.vlvolad.pendulumstudio.common.SphereGL;
import com.vlvolad.pendulumstudio.common.RodGL;
import com.vlvolad.pendulumstudio.common.SpringGL;
import com.vlvolad.pendulumstudio.common.TrajectoryGL;
import com.vlvolad.pendulumstudio.springpendulum2d.SP2DGLRenderer;

public class SpringMathematicalPendulum extends GenericPendulum {
    public volatile boolean moved;
	public volatile int moveIndex;
	double aa, l, m1, m2, g, k, ken, pen, pp;
	public volatile double gam;
	double qo[];
	boolean trmd;
	SphereGL mSphere, mSphere2;
	SpringGL mSpring;
	RodGL mRod;
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

    public SpringMathematicalPendulum(double aa, double k, double l, double m1, double m2,
			double s, double sv,
			double th1, double th2, double thv1,
			double thv2, double gr, double gam,
			boolean trmd, int trLength, boolean firsttime)
	{
		super();
        this.name = "Spring Mathematical Pendulum (2D)";
		this.firsttime = firsttime;
		g = gr;
		this.k = k;
		this.aa = aa;
		this.l = l;
		this.m1 = m1;
		this.m2 = m2;
		this.gam = gam;
		this.trmd = trmd;
		sz = 3;
		q = new double[sz];
		qv = new double[sz];
		qo = new double[sz];
		q[0] = s * Math.cos(th1);
		qv[0] = sv * Math.cos(th1) - s * Math.sin(th1) * thv1;
		qo[0] = q[0];
		q[1] = s * Math.sin(th1);
		qv[1] = sv * Math.sin(th1) + s * Math.cos(th1) * thv1;
		qo[1] = q[1];
		q[2] = th2;
		qv[2] = thv2;
		qo[2] = q[2];
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
		mRod = new RodGL((float)(l-10.*Math.pow(m2, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
		mSpring = new SpringGL((float)aa, 10);
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
		zoomIn = 0.6f;
		moveX = 0.f;
		moveY = 0.f;
		dynamicGravity = false;
		gy = gx = 0.f;
		gz = 981.f;
		mTrajectory = new TrajectoryGL(trLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(trLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
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
		g = SMPSimulationParameters.simParams.g;
		this.k = SMPSimulationParameters.simParams.k;
		this.aa = SMPSimulationParameters.simParams.aa * 100.;
		this.l = SMPSimulationParameters.simParams.l * 100.;
		this.m1 = SMPSimulationParameters.simParams.m1;
		this.m2 = SMPSimulationParameters.simParams.m2;
		this.gam = SMPSimulationParameters.simParams.gam;
		this.trmd = SMPSimulationParameters.simParams.showTrajectory;
		if (SMPSimulationParameters.simParams.initRandom) {
			double tr = aa + 0.5f*aa*(2.f*generator.nextDouble()-1.f);
			double tth = 2. * Math.PI * generator.nextDouble();
			q[0] = tr * Math.cos(tth);
			qv[0] = 0.f;
			q[1] = tr * Math.sin(tth);
			qv[1] = 0.f;
			q[2] = (float) (2. * Math.PI * generator.nextDouble()); // th
			qv[2] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
		} else {
			q[0] = SMPSimulationParameters.simParams.s * 100. * Math.cos(SMPSimulationParameters.simParams.th1);
			qv[0] = SMPSimulationParameters.simParams.sv * 100. * Math.cos(SMPSimulationParameters.simParams.th1) - SMPSimulationParameters.simParams.s * 100. * Math.sin(SMPSimulationParameters.simParams.th1) * SMPSimulationParameters.simParams.thv1;

			q[1] = SMPSimulationParameters.simParams.s * 100. * Math.sin(SMPSimulationParameters.simParams.th1);
			qv[1] = SMPSimulationParameters.simParams.sv * 100. * Math.sin(SMPSimulationParameters.simParams.th1) + SMPSimulationParameters.simParams.s * 100. * Math.cos(SMPSimulationParameters.simParams.th1) * SMPSimulationParameters.simParams.thv1;
			q[2] = SMPSimulationParameters.simParams.th2;
			qv[2] = SMPSimulationParameters.simParams.thv2;
		}
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mTrajectory = new TrajectoryGL(SMPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(SMPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
		mRod = new RodGL((float)(l-10.*Math.pow(m2, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
		mSpring = new SpringGL((float)aa, 10);
		timeInterval = SystemClock.uptimeMillis();
		moveIndex = 0;
		moved = false;
        setColorPendulum1(SMPSimulationParameters.simParams.pendulumColor);
        setColorPendulum2(SMPSimulationParameters.simParams.pendulumColor2);
	}

	void restartRandom(double l, double m, double gr, double k, boolean trmd,
			int trLength) {
        frames = 0;
        fps = 0.f;
		g = gr;
		this.k = SMPSimulationParameters.simParams.k;
		this.aa = SMPSimulationParameters.simParams.aa * 100.;
		this.l = SMPSimulationParameters.simParams.l * 100.;
		this.m1 = SMPSimulationParameters.simParams.m1;
		this.m2 = SMPSimulationParameters.simParams.m2;
		this.gam = SMPSimulationParameters.simParams.gam;
		this.trmd = SMPSimulationParameters.simParams.showTrajectory;
		if (SMPSimulationParameters.simParams.initRandom) {
			q[0] = (float)(aa + 0.5f*aa*(2.f*generator.nextDouble()-1.f));
			qv[0] = 0.f;
			q[1] = (float) (Math.PI / 1.25 * generator.nextDouble()); // th
			qv[1] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
			q[2] = (float) (Math.PI / 1.25 * generator.nextDouble()); // th
			qv[2] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
		} else {
			q[0] = SMPSimulationParameters.simParams.s;
			qv[0] = SMPSimulationParameters.simParams.sv;
			q[1] = SMPSimulationParameters.simParams.th1;
			qv[1] = SMPSimulationParameters.simParams.thv1;
			q[2] = SMPSimulationParameters.simParams.th2;
			qv[2] = SMPSimulationParameters.simParams.thv2;
		}
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mTrajectory = new TrajectoryGL(SMPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(SMPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
		timeInterval = SystemClock.uptimeMillis();
		moveIndex = 0;
		moved = false;
	}

	protected void accel(double a[], double qt[], double qvt[]) {
		if (!dynamicGravity)
			accel1(a, qt, qvt);
		else
			accel2(a, qt, qvt);
	}
	
	double det(double a1,double a2,double a3,double b1,double b2,double b3,double c1,double c2,double c3)
	{
		return a1*b2*c3 + a2*b3*c1 + a3*b1*c2 - a3*b2*c1 - a1*b3*c2 - a2*b1*c3;
	}

	void accel1(double a[], double qt[], double qvt[]) {
		 double A1,A2,A3,B1,B2,B3,C1,C2,C3,D1,D2,D3;

		 A1 = m1 + m2;
		 B1 = 0.;
		 C1 = -m2*l*Math.sin(qt[2]);
		 D1 = -m2*l*Math.cos(qt[2])*qvt[2]*qvt[2] 
				 + k*qt[0]*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]))
				 - (m1+m2)*g
				 + 2. * gam * qvt[0] - gam * l * Math.sin(qt[2])*qvt[2];
		 A2 = 0.;
		 B2 = (m1+m2);
		 C2 = m2*l*Math.cos(qt[2]);
		 D2 = -m2*l*Math.sin(qt[2])*qvt[2]*qvt[2] 
				 + k*qt[1]*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]))
				 + 2. * gam * qvt[1] + gam * l * Math.cos(qt[2])*qvt[2];
		 A3 = -m2*l*Math.sin(qt[2]);
		 B3 = m2*l*Math.cos(qt[2]);
		 C3 = m2*l*l;
		 D3 = m2*g*l*Math.sin(qt[2])
				 - gam*l*Math.sin(qt[2])*qvt[0] + gam*l*Math.cos(qt[2])*qvt[1] + gam*l*l*qvt[2];

		 a[0] = det(-D1,B1,C1,-D2,B2,C2,-D3,B3,C3)/det(A1,B1,C1,A2,B2,C2,A3,B3,C3);
		 a[1] = det(A1,-D1,C1,A2,-D2,C2,A3,-D3,C3)/det(A1,B1,C1,A2,B2,C2,A3,B3,C3);
		 a[2] = det(A1,B1,-D1,A2,B2,-D2,A3,B3,-D3)/det(A1,B1,C1,A2,B2,C2,A3,B3,C3);
	}

	void accel2(double a[], double qt[], double qvt[]) {
		double A1,A2,A3,B1,B2,B3,C1,C2,C3,D1,D2,D3;

		 A1 = m1 + m2;
		 B1 = 0.;
		 C1 = -m2*l*Math.sin(qt[2]);
		 D1 = -m2*l*Math.cos(qt[2])*qvt[2]*qvt[2] 
				 + k*qt[0]*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]))
				 - (m1+m2)*gz
				 + 2. * gam * qvt[0] - gam * l * Math.sin(qt[2])*qvt[2];
		 A2 = 0.;
		 B2 = (m1+m2);
		 C2 = m2*l*Math.cos(qt[2]);
		 D2 = -m2*l*Math.sin(qt[2])*qvt[2]*qvt[2] 
				 + k*qt[1]*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]))
				 - (m1+m2)*gy
				 + 2. * gam * qvt[1] + gam * l * Math.cos(qt[2])*qvt[2];

		 A3 = -m2*l*Math.sin(qt[2]);
		 B3 = m2*l*Math.cos(qt[2]);
		 C3 = m2*l*l;
		 D3 = m2*gz*l*Math.sin(qt[2])
				 - m2*gy*l*Math.cos(qt[2])
				 - gam*l*Math.sin(qt[2])*qvt[0] + gam*l*Math.cos(qt[2])*qvt[1] + gam*l*l*qvt[2];

		 
		 a[0] = det(-D1,B1,C1,-D2,B2,C2,-D3,B3,C3)/det(A1,B1,C1,A2,B2,C2,A3,B3,C3);
		 a[1] = det(A1,-D1,C1,A2,-D2,C2,A3,-D3,C3)/det(A1,B1,C1,A2,B2,C2,A3,B3,C3);
		 a[2] = det(A1,B1,-D1,A2,B2,-D2,A3,B3,-D3)/det(A1,B1,C1,A2,B2,C2,A3,B3,C3);
	}

	double kien() {
		return (0.5*m1*(qv[0]*qv[0]+q[0]*q[0]*qv[1]*qv[1])+0.5*m2*(qv[0]*qv[0]+q[0]*q[0]*qv[1]*qv[1]+l*l*qv[2]*qv[2]+2*l*Math.sin(q[1]-q[2])*qv[2]*qv[0]+
				 2*q[0]*l*Math.cos(q[1]-q[2])*qv[1]*qv[2]))/10000.0;
	}

	double poen() {
		return (0.5*k*(q[0]-aa)*(q[0]-aa)-(m1+m2)*g*q[0]*Math.cos(q[1])-m2*g*l*Math.cos(q[2]))/10000.0;
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
        return q[1];
    }

    double getyv1()
    {
    	return qv[1];
    }

    double getyo1()
    {
    	return qo[1];
    }

    double gety2()
    {
    	return q[1] + l * Math.sin(q[2]);
    }

    double getyv2()
    {
    	return qv[1] + l * Math.cos(q[2]) * qv[2];
    }

    double getyo2()
    {
    	return qo[1] + l * Math.sin(qo[2]);
    }

    double getz1()
    {
    	return q[0];
    }

    double getzv1()
    {
    	return qv[0];
    }

    double getzo1()
    {
    	return qo[0];
    }

    double getz2()
    {
    	return q[0] + l*Math.cos(q[2]);
    }

    double getzv2()
    {
    	return qv[0] - l*Math.sin(q[2]) * qv[2];
    }

    double getzo2()
    {
    	return qo[0] + l*Math.cos(qo[2]);
    }

    protected void integrate(double dt, int pre) {
        qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
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

    	l = Math.sqrt(x2*x2+y2*y2);

    	q[1] = x1;
    	q[0] = y1;
    	q[2] = Math.atan2(x2, y2);

		timeInterval2 = SystemClock.uptimeMillis();
    	moved = true;
    	mTrajectory.addPointToTrajectory((float)getx1(), (float)gety1(), (float)getz1());
    	mTrajectory2.addPointToTrajectory((float)getx2(), (float)gety2(), (float)getz2());
    	mRod = new RodGL((float)(l-10.*Math.pow(m1, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m1, 1./3.)), 30);
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

		//SMPGLRenderer.perspectiveGL(mProjMatrix, 45.0f,(float)(Width)/Height,0.1f,1200.0f);
		SMPGLRenderer.orthoGL(mProjMatrix, Width, Height);
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

		if (SMPSimulationParameters.simParams.showTrajectory && !SMPSimulationParameters.simParams.infiniteTrajectory)
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

		if (SMPSimulationParameters.simParams.showTrajectory && !SMPSimulationParameters.simParams.infiniteTrajectory)
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
		if (Math.atan2(y, x)>0.) Matrix.rotateM(mVMatrix, 0, (float)(180.f), 0.f, 0.f, 1.f);
		Matrix.scaleM(mVMatrix, 0, 1.f, 1.f, (float)Math.sqrt(q[0]*q[0]+q[1]*q[1]));
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mSpring.draw(mProgram, mMVPMatrix, mVMatrix);
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 1);
		
		Matrix.scaleM(mVMatrix, 0, 1.f, 1.f, 1.f/(float)Math.sqrt(q[0]*q[0]+q[1]*q[1]));
		if (Math.atan2(y, x)>0.) Matrix.rotateM(mVMatrix, 0, (float)(180.f), 0.f, 0.f, -1.f);
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
		mRod.draw(mProgram, mMVPMatrix, mVMatrix);
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

    public void clearTrajectory() {
        mTrajectory.clearTrajectory((float)getx1(), (float)gety1(), (float)getz1());
        mTrajectory2.clearTrajectory((float)getx2(), (float)gety2(), (float)getz2());
    }

    public void setDampingMode(boolean enabled) {
        if (enabled) this.gam = SMPSimulationParameters.simParams.gam;
        else this.gam = 0.;
    }

    public void setTraceMode(boolean enabled) {
        SMPSimulationParameters.simParams.showTrajectory = enabled;
    }
}
