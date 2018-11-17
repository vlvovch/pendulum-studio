package com.vlvolad.pendulumstudio.doublesphericalpendulum;

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
import com.vlvolad.pendulumstudio.common.SphereGL;
import com.vlvolad.pendulumstudio.common.RodGL;
import com.vlvolad.pendulumstudio.common.TrajectoryGL;

public class DoubleSphericalPendulum extends GenericPendulum {
	double l1, l2, m1, m2, g, ken, pen, pp;
    public volatile boolean moved;
	public volatile double k;
	double qo[];
	boolean trmd;
	SphereGL mSphere, mSphere2;
	RodGL mRod, mRod2;
	private FloatBuffer lineVertexBuffer;
	long timeInterval;
	public volatile float zoomIn;
	public volatile float moveX;
	public volatile float moveY;
	int coordSystem;

    final float[] mRotationMatrix = new float[16];
	
	private FloatBuffer lightDir, lightHP, lightAC, lightDC, lightSC;
	private FloatBuffer materialAF, materialDF, materialSF;
	float materialshin;

	public static Random generator = new Random();

    public DoubleSphericalPendulum(double l1, double l2, double m1, double m2,
			double th1, double az1, double th2, double az2, double thv1,
			double azv1, double thv2, double azv2, double gr, double k,
			boolean trmd, int trLength, boolean firsttime)
	{
		super();
        this.name = "Double Spherical Pendulum (3D)";
		this.firsttime = firsttime;
		g = gr;
		this.k = k;
		this.l1 = l1;
		this.l2 = l2;
		this.m1 = m1;
		this.m2 = m2;
		this.trmd = trmd;
		sz = 4;
		q = new double[sz];
		qv = new double[sz];
		qo = new double[sz];
		q[0] = th1;
		q[1] = az1;
		qv[0] = thv1;
		qv[1] = azv1;
		qo[0] = q[0];
		qo[1] = q[1];
		q[2] = th2;
		q[3] = az2;
		qv[2] = thv2;
		qv[3] = azv2;
		qo[2] = q[2];
		qo[3] = q[3];
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
		zoomIn = 0.55f;
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
		g = DSPSimulationParameters.simParams.g;
		this.k = DSPSimulationParameters.simParams.k;
		this.l1 = DSPSimulationParameters.simParams.l1 * 100.;
		this.l2 = DSPSimulationParameters.simParams.l2 * 100.;
		this.m1 = DSPSimulationParameters.simParams.m1;
		this.m2 = DSPSimulationParameters.simParams.m2;
		this.trmd = DSPSimulationParameters.simParams.showTrajectory;
		if (DSPSimulationParameters.simParams.initRandom) {
            q[0] = (float) Math.acos(0.999999 * (2. * generator.nextDouble() - 1.));
			q[1] = (float) (2. * Math.PI * generator.nextDouble()); // az
			qv[0] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
			qv[1] = (float) (Math.PI * generator.nextDouble()); // azv
            q[2] = (float) Math.acos(0.999999 * (2. * generator.nextDouble() - 1.));
			q[3] = (float) (2. * Math.PI * generator.nextDouble()); // az
			qv[2] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
			qv[3] = (float) (Math.PI * generator.nextDouble()); // azv
		} else {
			q[0] = DSPSimulationParameters.simParams.th1;
			q[1] = DSPSimulationParameters.simParams.ph1;
			qv[0] = DSPSimulationParameters.simParams.thv1;
			qv[1] = DSPSimulationParameters.simParams.phv1;
			q[2] = DSPSimulationParameters.simParams.th2;
			q[3] = DSPSimulationParameters.simParams.ph2;
			qv[2] = DSPSimulationParameters.simParams.thv2;
			qv[3] = DSPSimulationParameters.simParams.phv2;
		}
		coordSystem = 0;
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		qo[3] = q[3];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mTrajectory = new TrajectoryGL(DSPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(DSPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
		mRod = new RodGL((float)(l1-10.*Math.pow(m1, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m1, 1./3.)), 30);
		mRod2 = new RodGL((float)(l2-10.*Math.pow(m1, 1./3.)-10.*Math.pow(m2, 1./3.)), (float)(10.*Math.pow(m1, 1./3.)), (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
		timeInterval = SystemClock.uptimeMillis();
        setColorPendulum1(DSPSimulationParameters.simParams.pendulumColor);
        setColorPendulum2(DSPSimulationParameters.simParams.pendulumColor2);
	}

	void restartRandom(double l, double m, double gr, double k, boolean trmd,
			int trLength) {
        frames = 0;
        fps = 0.f;
		g = gr;
		this.k = DSPSimulationParameters.simParams.k;
		this.l1 = DSPSimulationParameters.simParams.l1;
		this.l2 = DSPSimulationParameters.simParams.l2;
		this.m1 = DSPSimulationParameters.simParams.m1;
		this.m2 = DSPSimulationParameters.simParams.m2;
		this.trmd = trmd;
		coordSystem = 0;
		q[0] = (float) (Math.PI / 1.25 * generator.nextDouble()); // th
		q[1] = (float) (2. * Math.PI * generator.nextDouble()); // az
		qv[0] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
		qv[1] = (float) (Math.PI * generator.nextDouble()); // azv
		q[2] = (float) (Math.PI / 1.25 * generator.nextDouble()); // th
		q[3] = (float) (2. * Math.PI * generator.nextDouble()); // az
		qv[2] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
		qv[3] = (float) (Math.PI * generator.nextDouble()); // azv
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		qo[3] = q[3];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mTrajectory = new TrajectoryGL(DSPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(DSPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
		timeInterval = SystemClock.uptimeMillis();
		coordSystem = 0;
	}

	protected void accel(double a[], double qt[], double qvt[]) {
		if (!dynamicGravity)
			accel1(a, qt, qvt);
		else
			accel2(a, qt, qvt);
	}

	void accel1(double a[], double qt[], double qvt[]) {
		double costh1 = Math.cos(qt[0]);
	    double sinth1 = Math.sin(qt[0]);
	    double costh2 = Math.cos(qt[2]);
	    double sinth2 = Math.sin(qt[2]);
	    double cosph1 = Math.cos(qt[1]);
	    double sinph1 = Math.sin(qt[1]);
	    double cosph2 = Math.cos(qt[3]);
	    double sinph2 = Math.sin(qt[3]);
	    double cosdphi = Math.cos(qt[1] - qt[3]);
	    double sindphi = Math.sin(qt[1] - qt[3]);
	    double coefs[][];
	    double bcoefs[];
	    coefs = new double[4][4];
	    coefs[0][0] = (m1+m2)*l1*l1;
	    coefs[0][1] = 0.;
	    coefs[0][2] = m2*l1*l2*(cosdphi*costh1*costh2 + sinth1*sinth2);
	    coefs[0][3] = m2*l1*l2*sindphi*costh1*sinth2;
	    coefs[1][0] = 0.;
	    coefs[1][1] = (m1+m2)*l1*l1*sinth1*sinth1;
	    coefs[1][2] = -m2*l1*l2*sindphi*sinth1*costh2;
	    coefs[1][3] = m2*l1*l2*cosdphi*sinth1*sinth2;
	    coefs[2][0] = m2*l1*l2*(cosdphi*costh1*costh2 + sinth1*sinth2);
	    coefs[2][1] = -m2*l1*l2*sindphi*sinth1*costh2;
	    coefs[2][2] = m2*l2*l2;
	    coefs[2][3] = 0.;
	    coefs[3][0] = m2*l1*l2*sindphi*costh1*sinth2;
	    coefs[3][1] = m2*l1*l2*cosdphi*sinth1*sinth2;
	    coefs[3][2] = 0.;
	    coefs[3][3] = m2*l2*l2*sinth2*sinth2;
	    bcoefs = new double[4];
	    bcoefs[0] = (m1+m2)*l1*l1*sinth1*costh1*qvt[1]*qvt[1]
	                -m2*l1*l2*(2.*sindphi*costh1*costh2*qvt[3]*qvt[2]
	                           -cosdphi*costh1*sinth2*qvt[2]*qvt[2]
	                           +sinth1*costh2*qvt[2]*qvt[2]
	                           -cosdphi*costh1*sinth2*qvt[3]*qvt[3]);
	    if (coordSystem==0) bcoefs[0] += -(m1+m2)*g*l1*sinth1;
	    else if (coordSystem==1) bcoefs[0] += (m1+m2)*g*l1*cosph1*costh1;
	    else bcoefs[0] += (m1+m2)*g*l1*sinph1*costh1;
	    bcoefs[0] -= 2*k*l1*l1*qvt[0] + k*l1*l2*(sindphi*costh1*sinth2*qvt[3]
	                                             +(cosdphi*costh1*costh2+sinth1*sinth2)*qvt[2]);
	    bcoefs[1] = -(m1+m2)*l1*l1*2.*sinth1*costh1*qvt[0]*qvt[1]
	            -m2*l1*l2*(sindphi*sinth1*sinth2*qvt[3]*qvt[3]
	                       +2.*cosdphi*sinth1*costh2*qvt[2]*qvt[3]
	                       +sindphi*sinth1*sinth2*qvt[2]*qvt[2]);
	    if (coordSystem==1) bcoefs[1] += -(m1+m2)*g*l1*sinph1*sinth1;
	    else if (coordSystem==2) bcoefs[1] += (m1+m2)*g*l1*cosph1*sinth1;
	    bcoefs[1] -= 2*k*l1*l1*sinth1*sinth1*qvt[1]+ k*l1*l2*(cosdphi*sinth1*sinth2*qvt[3]
	                                             -sindphi*sinth1*costh2*qvt[2]);
	    bcoefs[2] = m2*l2*l2*sinth2*costh2*qvt[3]*qvt[3]
	            -m2*l1*l2*(-2.*sindphi*costh1*costh2*qvt[1]*qvt[0]
	                       -cosdphi*sinth1*costh2*qvt[0]*qvt[0]
	                       +costh1*sinth2*qvt[0]*qvt[0]
	                       -cosdphi*sinth1*costh2*qvt[1]*qvt[1]);
	    if (coordSystem==0) bcoefs[2] += -m2*g*l2*sinth2;
	    else if (coordSystem==1) bcoefs[2] += m2*g*l2*cosph2*costh2;
	    else bcoefs[2] += m2*g*l2*sinph2*costh2;
	    bcoefs[2] -= k*l2*l2*qvt[2]+ k*l1*l2*(-sindphi*costh1*sinth2*qvt[1]
	                                             +(cosdphi*costh1*costh2+sinth1*sinth2)*qvt[0]);
	    bcoefs[3] = -m2*l2*l2*2.*sinth2*costh2*qvt[2]*qvt[3]
	            -m2*l1*l2*(-sindphi*sinth1*sinth2*qvt[1]*qvt[1]
	                       +2.*cosdphi*costh1*sinth2*qvt[0]*qvt[1]
	                       -sindphi*sinth1*sinth2*qvt[0]*qvt[0]);
	    if (coordSystem==1) bcoefs[3] += -m2*g*l2*sinph2*sinth2;
	    else if (coordSystem==2) bcoefs[3] += m2*g*l2*cosph2*sinth2;
	    bcoefs[3] -= k*l2*l2*sinth2*sinth2*qvt[3] + k*l1*l2*(cosdphi*sinth1*sinth2*qvt[1]
	                                             +sindphi*sinth2*costh1*qvt[0]);
	    LESolver syst;
	    syst = new LESolver(4, coefs, bcoefs);
	    double[] res = syst.Solve();

	    for(int i=0;i<4;++i) a[i] = res[i];
	}

	void accel2(double a[], double qt[], double qvt[]) {
		double costh1 = Math.cos(qt[0]);
	    double sinth1 = Math.sin(qt[0]);
	    double costh2 = Math.cos(qt[2]);
	    double sinth2 = Math.sin(qt[2]);
	    double cosph1 = Math.cos(qt[1]);
	    double sinph1 = Math.sin(qt[1]);
	    double cosph2 = Math.cos(qt[3]);
	    double sinph2 = Math.sin(qt[3]);
	    double cosdphi = Math.cos(qt[1] - qt[3]);
	    double sindphi = Math.sin(qt[1] - qt[3]);
	    double coefs[][];
	    double bcoefs[];
	    coefs = new double[4][4];
	    coefs[0][0] = (m1+m2)*l1*l1;
	    coefs[0][1] = 0.;
	    coefs[0][2] = m2*l1*l2*(cosdphi*costh1*costh2 + sinth1*sinth2);
	    coefs[0][3] = m2*l1*l2*sindphi*costh1*sinth2;
	    coefs[1][0] = 0.;
	    coefs[1][1] = (m1+m2)*l1*l1*sinth1*sinth1;
	    coefs[1][2] = -m2*l1*l2*sindphi*sinth1*costh2;
	    coefs[1][3] = m2*l1*l2*cosdphi*sinth1*sinth2;
	    coefs[2][0] = m2*l1*l2*(cosdphi*costh1*costh2 + sinth1*sinth2);
	    coefs[2][1] = -m2*l1*l2*sindphi*sinth1*costh2;
	    coefs[2][2] = m2*l2*l2;
	    coefs[2][3] = 0.;
	    coefs[3][0] = m2*l1*l2*sindphi*costh1*sinth2;
	    coefs[3][1] = m2*l1*l2*cosdphi*sinth1*sinth2;
	    coefs[3][2] = 0.;
	    coefs[3][3] = m2*l2*l2*sinth2*sinth2;
	    bcoefs = new double[4];
	    bcoefs[0] = (m1+m2)*l1*l1*sinth1*costh1*qvt[1]*qvt[1]
	                -m2*l1*l2*(2.*sindphi*costh1*costh2*qvt[3]*qvt[2]
	                           -cosdphi*costh1*sinth2*qvt[2]*qvt[2]
	                           +sinth1*costh2*qvt[2]*qvt[2]
	                           -cosdphi*costh1*sinth2*qvt[3]*qvt[3]);
	    if (coordSystem==0) bcoefs[0] += (m1+m2)*l1*(gx*cosph1*costh1+gy*sinph1*costh1-gz*sinth1);
	    else if (coordSystem==1) bcoefs[0] += (m1+m2)*l1*(gz*cosph1*costh1+gx*sinph1*costh1-gy*sinth1);
	    else bcoefs[0] += (m1+m2)*l1*(gy*cosph1*costh1+gz*sinph1*costh1-gx*sinth1);
	    bcoefs[0] -= 2*k*l1*l1*qvt[0] + k*l1*l2*(sindphi*costh1*sinth2*qvt[3]
	                                             +(cosdphi*costh1*costh2+sinth1*sinth2)*qvt[2]);
	    bcoefs[1] = -(m1+m2)*l1*l1*2.*sinth1*costh1*qvt[0]*qvt[1]
	            -m2*l1*l2*(sindphi*sinth1*sinth2*qvt[3]*qvt[3]
	                       +2.*cosdphi*sinth1*costh2*qvt[2]*qvt[3]
	                       +sindphi*sinth1*sinth2*qvt[2]*qvt[2]);
	    if (coordSystem==0) bcoefs[1] += -(m1+m2)*gx*l1*sinph1*sinth1+(m1+m2)*gy*l1*cosph1*sinth1;
	    else if (coordSystem==1) bcoefs[1] += -(m1+m2)*gz*l1*sinph1*sinth1+(m1+m2)*gx*l1*cosph1*sinth1;
	    else bcoefs[1] += -(m1+m2)*gy*l1*sinph1*sinth1+(m1+m2)*gz*l1*cosph1*sinth1;
	    bcoefs[1] -= 2*k*l1*l1*sinth1*sinth1*qvt[1]+ k*l1*l2*(cosdphi*sinth1*sinth2*qvt[3]
	                                             -sindphi*sinth1*costh2*qvt[2]);
	    bcoefs[2] = m2*l2*l2*sinth2*costh2*qvt[3]*qvt[3]
	            -m2*l1*l2*(-2.*sindphi*costh1*costh2*qvt[1]*qvt[0]
	                       -cosdphi*sinth1*costh2*qvt[0]*qvt[0]
	                       +costh1*sinth2*qvt[0]*qvt[0]
	                       -cosdphi*sinth1*costh2*qvt[1]*qvt[1]);
	    if (coordSystem==0) bcoefs[2] += m2*l2*(gx*cosph2*costh2+gy*sinph2*costh2-gz*sinth2);
	    else if (coordSystem==1) bcoefs[2] += m2*l2*(gz*cosph2*costh2+gx*sinph2*costh2-gy*sinth2);
	    else bcoefs[2] += m2*l2*(gy*cosph2*costh2+gz*sinph2*costh2-gx*sinth2);
	    bcoefs[2] -= k*l2*l2*qvt[2]+ k*l1*l2*(-sindphi*costh1*sinth2*qvt[1]
	                                             +(cosdphi*costh1*costh2+sinth1*sinth2)*qvt[0]);
	    bcoefs[3] = -m2*l2*l2*2.*sinth2*costh2*qvt[2]*qvt[3]
	            -m2*l1*l2*(-sindphi*sinth1*sinth2*qvt[1]*qvt[1]
	                       +2.*cosdphi*costh1*sinth2*qvt[0]*qvt[1]
	                       -sindphi*sinth1*sinth2*qvt[0]*qvt[0]);
	    if (coordSystem==0) bcoefs[3] += -m2*gx*l2*sinph2*sinth2+m2*gy*l2*cosph2*sinth2;
	    else if (coordSystem==1) bcoefs[3] += -m2*gz*l2*sinph2*sinth2+m2*gx*l2*cosph2*sinth2;
	    else bcoefs[3] += -m2*gy*l2*sinph2*sinth2+m2*gz*l2*cosph2*sinth2;
	    bcoefs[3] -= k*l2*l2*sinth2*sinth2*qvt[3] + k*l1*l2*(cosdphi*sinth1*sinth2*qvt[1]
	                                             +sindphi*sinth2*costh1*qvt[0]);
	    LESolver syst;
	    syst = new LESolver(4, coefs, bcoefs);
	    double[] res = syst.Solve();
	    for(int i=0;i<4;++i) a[i] = res[i];
	}

	void changeCoordinates() {
		int csyst = coordSystem;
	    csyst = (csyst+1)%3;
	    while (csyst!=coordSystem)
	    {
	        double tx1, tx2, ty1, ty2, tz1, tz2;
	        double txv1, txv2, tyv1, tyv2, tzv1, tzv2;
	        if (csyst==0)
	        {
	            tx1 = getx1();
	            ty1 = gety1();
	            tz1 = getz1();
	            tx2 = getx2();
	            ty2 = gety2();
	            tz2 = getz2();
	            txv1 = getxv1();
	            tyv1 = getyv1();
	            tzv1 = getzv1();
	            txv2 = getxv2();
	            tyv2 = getyv2();
	            tzv2 = getzv2();
	        }
	        else if (csyst==1)
	        {
	            tx1 = getz1();
	            ty1 = getx1();
	            tz1 = gety1();
	            tx2 = getz2();
	            ty2 = getx2();
	            tz2 = gety2();
	            txv1 = getzv1();
	            tyv1 = getxv1();
	            tzv1 = getyv1();
	            txv2 = getzv2();
	            tyv2 = getxv2();
	            tzv2 = getyv2();
	        }
	        else
	        {
	            tx1 = gety1();
	            ty1 = getz1();
	            tz1 = getx1();
	            tx2 = gety2();
	            ty2 = getz2();
	            tz2 = getx2();
	            txv1 = getyv1();
	            tyv1 = getzv1();
	            tzv1 = getxv1();
	            txv2 = getyv2();
	            tyv2 = getzv2();
	            tzv2 = getxv2();
	        }
	        double th1, ph1, th2, ph2, thv1, phv1, thv2, phv2;
	        th1 = Math.acos(tz1 / l1);
	        if (Math.abs(Math.cos(th1))>0.8)
	        {
	            csyst = (csyst + 1)%3;
	            continue;
	        }
	        ph1 = Math.atan2(ty1, tx1);
	        thv1 = -tzv1 / l1 / Math.sin(th1);
	        phv1 = (tx1*tyv1 - ty1*txv1) / (tx1*tx1 + ty1*ty1);
	        double tx2t, ty2t, tz2t;
	        double txv2t, tyv2t, tzv2t;
	        tx2t = tx2 - l1*Math.cos(ph1)*Math.sin(th1);
	        ty2t = ty2 - l1*Math.sin(ph1)*Math.sin(th1);
	        tz2t = tz2 - l1*Math.cos(th1);
	        txv2t = txv2 - l1*(-Math.sin(ph1)*Math.sin(th1)*phv1 + Math.cos(ph1)*Math.cos(th1)*thv1);
	        tyv2t = tyv2 - l1*(Math.cos(ph1)*Math.sin(th1)*phv1 + Math.sin(ph1)*Math.cos(th1)*thv1);
	        tzv2t = tzv2 + l1*Math.sin(th1)*thv1;
	        th2 = Math.acos(tz2t / l2);
	        if (Math.abs(Math.cos(th2))>0.8)
	        {
	            csyst = (csyst + 1)%3;
	            continue;
	        }
	        ph2 = Math.atan2(ty2t, tx2t);
	        thv2 = -tzv2t / l2 / Math.sin(th2);
	        phv2 = (tx2t*tyv2t - ty2t*txv2t) / (tx2t*tx2t + ty2t*ty2t);
	        q[0] = th1;
	        q[1] = ph1;
	        q[2] = th2;
	        q[3] = ph2;
	        qv[0] = thv1;
	        qv[1] = phv1;
	        qv[2] = thv2;
	        qv[3] = phv2;
	        coordSystem = csyst;
	        return;
	    }
	}

	double kien() {
		return (0.5*(m1+m2)*l1*l1*(Math.sin(q[0])*Math.sin(q[0])*qv[1]*qv[1] + qv[0]*qv[0])
                +0.5*m2*l2*l2*(Math.sin(q[2])*Math.sin(q[2])*qv[3]*qv[3] + qv[2]*qv[2])
                +l1*l2*m2*(Math.cos(q[1]-q[3])*Math.sin(q[0])*Math.sin(q[2])*qv[1]*qv[3]
                             +Math.cos(q[1]-q[3])*Math.cos(q[0])*Math.cos(q[2])*qv[0]*qv[2]
                             +Math.sin(q[3]-q[1])*Math.sin(q[0])*Math.cos(q[2])*qv[1]*qv[2]
                             +Math.sin(q[1]-q[3])*Math.cos(q[0])*Math.sin(q[2])*qv[0]*qv[3]
                             +Math.sin(q[0])*Math.sin(q[2])*qv[0]*qv[2]))/10000.0;
	}

	double poen() {
		if (coordSystem==0) return (-m1*g*l1*Math.cos(q[0])-m2*g*(l1*Math.cos(q[0])+l2*Math.cos(q[2])))/10000.0;
        else if (coordSystem==1) return (-m1*g*l1*Math.cos(q[1])*Math.sin(q[0])-m2*g*(l1*Math.cos(q[1])*Math.sin(q[0])+l2*Math.cos(q[3])*Math.sin(q[2])))/10000.0;
        else return (-m1*g*l1*Math.sin(q[1])*Math.sin(q[0])-m2*g*(l1*Math.sin(q[1])*Math.sin(q[0])+l2*Math.sin(q[3])*Math.sin(q[2])))/10000.0;
	}

	double ener() {
		return kien() + poen();
	}

	double getx1()
    {
        if (coordSystem==0) return l1*Math.cos(q[1])*Math.sin(q[0]);
        else if (coordSystem==1) return l1*Math.sin(q[1])*Math.sin(q[0]);
        else return l1*Math.cos(q[0]);
    }

    double getxv1()
    {
        if (coordSystem==0) return -l1*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l1*Math.cos(q[1])*Math.cos(q[0])*qv[0];
        else if (coordSystem==1) return l1*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l1*Math.sin(q[1])*Math.cos(q[0])*qv[0];
        else return -l1*Math.sin(q[0])*qv[0];
    }

    double getxo1()
    {
        if (coordSystem==0) return l1*Math.cos(qo[1])*Math.sin(qo[0]);
        else if (coordSystem==1) return l1*Math.sin(qo[1])*Math.sin(qo[0]);
        else return l1*Math.cos(qo[0]);
    }

    double getx2()
    {
        if (coordSystem==0) return l1*Math.cos(q[1])*Math.sin(q[0]) + l2*Math.cos(q[3])*Math.sin(q[2]);
        else if (coordSystem==1) return l1*Math.sin(q[1])*Math.sin(q[0]) + l2*Math.sin(q[3])*Math.sin(q[2]);
        else return l1*Math.cos(q[0]) + l2*Math.cos(q[2]);
    }

    double getxv2()
    {
        if (coordSystem==0) return -l1*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l1*Math.cos(q[1])*Math.cos(q[0])*qv[0]
                -l2*Math.sin(q[3])*Math.sin(q[2])*qv[3] + l2*Math.cos(q[3])*Math.cos(q[2])*qv[2];
        else if (coordSystem==1) return l1*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l1*Math.sin(q[1])*Math.cos(q[0])*qv[0]
                +l2*Math.cos(q[3])*Math.sin(q[2])*qv[3] + l2*Math.sin(q[3])*Math.cos(q[2])*qv[2];
        else return -l1*Math.sin(q[0])*qv[0] - l2*Math.sin(q[2])*qv[2];
    }

    double getxo2()
    {
        if (coordSystem==0) return l1*Math.cos(qo[1])*Math.sin(qo[0]) + l2*Math.cos(qo[3])*Math.sin(qo[2]);
        else if (coordSystem==1) return l1*Math.sin(qo[1])*Math.sin(qo[0]) + l2*Math.sin(qo[3])*Math.sin(qo[2]);
        else return l1*Math.cos(qo[0]) + l2*Math.cos(qo[2]);
    }

    double gety1()
    {
        if (coordSystem==0) return l1*Math.sin(q[1])*Math.sin(q[0]);
        else if (coordSystem==1) return l1*Math.cos(q[0]);
        else return l1*Math.cos(q[1])*Math.sin(q[0]);
    }

    double getyv1()
    {
        if (coordSystem==0) return l1*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l1*Math.sin(q[1])*Math.cos(q[0])*qv[0];
        else if (coordSystem==1) return -l1*Math.sin(q[0])*qv[0];
        else return -l1*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l1*Math.cos(q[1])*Math.cos(q[0])*qv[0];
    }

    double getyo1()
    {
        if (coordSystem==0) return l1*Math.sin(qo[1])*Math.sin(qo[0]);
        else if (coordSystem==1) return l1*Math.cos(qo[0]);
        else return l1*Math.cos(qo[1])*Math.sin(qo[0]);
    }

    double gety2()
    {
        if (coordSystem==0) return l1*Math.sin(q[1])*Math.sin(q[0]) + l2*Math.sin(q[3])*Math.sin(q[2]);
        else if (coordSystem==1) return l1*Math.cos(q[0]) + l2*Math.cos(q[2]);
        else return l1*Math.cos(q[1])*Math.sin(q[0]) + l2*Math.cos(q[3])*Math.sin(q[2]);
    }

    double getyv2()
    {
        if (coordSystem==0) return l1*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l1*Math.sin(q[1])*Math.cos(q[0])*qv[0]
                +l2*Math.cos(q[3])*Math.sin(q[2])*qv[3] + l2*Math.sin(q[3])*Math.cos(q[2])*qv[2];
        else if (coordSystem==1) return -l1*Math.sin(q[0])*qv[0] - l2*Math.sin(q[2])*qv[2];
        else return -l1*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l1*Math.cos(q[1])*Math.cos(q[0])*qv[0]
                -l2*Math.sin(q[3])*Math.sin(q[2])*qv[3] + l2*Math.cos(q[3])*Math.cos(q[2])*qv[2];
    }

    double getyo2()
    {
        if (coordSystem==0) return l1*Math.sin(qo[1])*Math.sin(qo[0]) + l2*Math.sin(qo[3])*Math.sin(qo[2]);
        else if (coordSystem==1) return l1*Math.cos(qo[0]) + l2*Math.cos(qo[2]);
        else return l1*Math.cos(qo[1])*Math.sin(qo[0]) + l2*Math.cos(qo[3])*Math.sin(qo[2]);
    }

    double getz1()
    {
        if (coordSystem==0) return l1*Math.cos(q[0]);
        else if (coordSystem==1) return l1*Math.cos(q[1])*Math.sin(q[0]);
        else return l1*Math.sin(q[1])*Math.sin(q[0]);
    }

    double getzv1()
    {
        if (coordSystem==0) return -l1*Math.sin(q[0])*qv[0];
        else if (coordSystem==1) return -l1*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l1*Math.cos(q[1])*Math.cos(q[0])*qv[0];
        else return l1*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l1*Math.sin(q[1])*Math.cos(q[0])*qv[0];
    }

    double getzo1()
    {
        if (coordSystem==0) return l1*Math.cos(qo[0]);
        else if (coordSystem==1) return l1*Math.cos(qo[1])*Math.sin(qo[0]);
        else return l1*Math.sin(qo[1])*Math.sin(qo[0]);
    }

    double getz2()
    {
        if (coordSystem==0) return l1*Math.cos(q[0]) + l2*Math.cos(q[2]);
        else if (coordSystem==1) return l1*Math.cos(q[1])*Math.sin(q[0]) + l2*Math.cos(q[3])*Math.sin(q[2]);
        else return l1*Math.sin(q[1])*Math.sin(q[0]) + l2*Math.sin(q[3])*Math.sin(q[2]);
    }

    double getzv2()
    {
        if (coordSystem==0) return -l1*Math.sin(q[0])*qv[0] - l2*Math.sin(q[2])*qv[2];
        else if (coordSystem==1) return -l1*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l1*Math.cos(q[1])*Math.cos(q[0])*qv[0]
                -l2*Math.sin(q[3])*Math.sin(q[2])*qv[3] + l2*Math.cos(q[3])*Math.cos(q[2])*qv[2];
        else return l1*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l1*Math.sin(q[1])*Math.cos(q[0])*qv[0]
                +l2*Math.cos(q[3])*Math.sin(q[2])*qv[3] + l2*Math.sin(q[3])*Math.cos(q[2])*qv[2];
    }

    double getzo2()
    {
        if (coordSystem==0) return l1*Math.cos(qo[0]) + l2*Math.cos(qo[2]);
        else if (coordSystem==1) return l1*Math.cos(qo[1])*Math.sin(qo[0]) + l2*Math.cos(qo[3])*Math.sin(qo[2]);
        else return l1*Math.sin(qo[1])*Math.sin(qo[0]) + l2*Math.sin(qo[3])*Math.sin(qo[2]);
    }

    protected void integrate(double dt, int pre) {
		if (Math.abs(Math.cos(q[0]))>0.8 || Math.abs(Math.cos(q[2]))>0.8) changeCoordinates();
        qo[0] = q[0];
		qo[1] = q[1];
        qo[2] = q[2];
        qo[3] = q[3];
		super.integrate(dt, pre);
		mTrajectory.addPointToTrajectory((float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2.addPointToTrajectory((float)getx2(), (float)gety2(), (float)getz2());
	}

	public void translateMVMatrix(float[] MVMatrix, int Width, int Height) {
		Matrix.setIdentityM(MVMatrix, 0);
		Matrix.translateM(MVMatrix, 0, 0.f, 10.f, -400.0f / zoomIn);
		Matrix.translateM(MVMatrix, 0, 0.f, 30.f*Height/3000.f, 0.f);
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
		if (elap < 0 || elap > 50)
			elap = 1;
		if (!paused) integrate(elap / 1000., 10);
	}

    public void draw(GL10 unused, int Width, int Height) {
		
		DSPGLRenderer.perspectiveGL(mProjMatrix, 45.0f,(float)(Width)/Height,0.1f,1200.0f);
	    Matrix.setIdentityM(mVMatrix, 0);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		Matrix.translateM(mVMatrix, 0, 0.f, 10.f, -400.0f / zoomIn);
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

		if (DSPSimulationParameters.simParams.showTrajectory && !DSPSimulationParameters.simParams.infiniteTrajectory)
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

		if (DSPSimulationParameters.simParams.showTrajectory && !DSPSimulationParameters.simParams.infiniteTrajectory)
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

    public void clearTrajectory() {
        mTrajectory.clearTrajectory((float)getx1(), (float)gety1(), (float)getz1());
        mTrajectory2.clearTrajectory((float)getx2(), (float)gety2(), (float)getz2());
    }

    public void setDampingMode(boolean enabled) {
        if (enabled) this.k = DSPSimulationParameters.simParams.k;
        else this.k = 0.;
    }

    public void setTraceMode(boolean enabled) {
        DSPSimulationParameters.simParams.showTrajectory = enabled;
    }
}
