package com.vlvolad.pendulumstudio.springsphericalpendulum;

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
import com.vlvolad.pendulumstudio.common.SpringGL;
import com.vlvolad.pendulumstudio.common.TrajectoryGL;

public class SpringSphericalPendulum extends GenericPendulum {
	double aa, l, m1, m2, g, k, ken, pen, pp;
    public volatile boolean moved;
	public volatile double gam;
	double qo[];
	boolean trmd;
	SphereGL mSphere, mSphere2;
	RodGL mRod;
	SpringGL mSpring;
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

    public SpringSphericalPendulum(double aa, double l, double m1, double m2,
			double k, double x, double y, double z, double th, double az,
			double xv, double yv, double zv, double thv, double azv,
			double gr, double gam,
			boolean trmd, int trLength, boolean firsttime)
	{
		super();
        this.name = "Spring Spherical Pendulum (3D)";
		this.firsttime = firsttime;
		g = gr;
		this.k = k;
		this.gam = gam;
		this.l = l;
		this.aa = aa;
		this.m1 = m1;
		this.m2 = m2;
		this.trmd = trmd;
		sz = 5;
		q = new double[sz];
		qv = new double[sz];
		qo = new double[sz];
		q[0] = x;
	    q[1] = y;
	    q[2] = z;
	    q[3] = th;
	    q[4] = az;
	    qv[0] = xv;
	    qv[1] = yv;
	    qv[2] = zv;
	    qv[3] = thv;
	    qv[4] = azv;
	    qo[0] = q[0];
	    qo[1] = q[1];
	    qo[2] = q[2];
	    qo[3] = q[3];
	    qo[4] = q[4];
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
		zoomIn = 0.5f;
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
		g = SSPSimulationParameters.simParams.g;
		this.k = SSPSimulationParameters.simParams.k;
		this.gam = SSPSimulationParameters.simParams.gam;
		this.l = SSPSimulationParameters.simParams.l * 100.;
		this.aa = SSPSimulationParameters.simParams.aa * 100.;
		this.m1 = SSPSimulationParameters.simParams.m1;
		this.m2 = SSPSimulationParameters.simParams.m2;
		this.trmd = SSPSimulationParameters.simParams.showTrajectory;
		if (SSPSimulationParameters.simParams.initRandom) {
			float tr = (float)(aa + 0.5f*aa*(2.f*generator.nextDouble()-1.f));
			float tth = (float)(Math.acos(0.999999 * (2.f*generator.nextDouble()-1.f)));
			float tphi = (float) (2. * Math.PI * generator.nextDouble()); 
			q[0] = tr * Math.cos(tphi) * Math.sin(tth);
			q[1] = tr * Math.sin(tphi) * Math.sin(tth);
			q[2] = tr * Math.cos(tth);
			qv[0] = qv[1] = qv[2] = 0.;
            q[3] = (float) Math.acos(0.999999 * (2. * generator.nextDouble() - 1.));
			q[4] = (float) (2. * Math.PI * generator.nextDouble()); // az
			qv[3] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
			qv[4] = (float) (Math.PI * generator.nextDouble()); // azv
		} else {
			q[0] = SSPSimulationParameters.simParams.x * 100.;
			q[1] = SSPSimulationParameters.simParams.y * 100.;
			q[2] = SSPSimulationParameters.simParams.z * 100.;
			qv[0] = SSPSimulationParameters.simParams.xv * 100.;
			qv[1] = SSPSimulationParameters.simParams.yv * 100.;
			qv[2] = SSPSimulationParameters.simParams.zv * 100.;
			q[3] = SSPSimulationParameters.simParams.th1;
			q[4] = SSPSimulationParameters.simParams.ph1;
			qv[3] = SSPSimulationParameters.simParams.thv1;
			qv[4] = SSPSimulationParameters.simParams.phv1;
		}
		coordSystem = 0;
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		qo[3] = q[3];
		qo[4] = q[4];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mRod = new RodGL((float)(l-10.*Math.pow(m2, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
		mSpring = new SpringGL((float)aa, 10);
		mTrajectory = new TrajectoryGL(SSPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(SSPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
		timeInterval = SystemClock.uptimeMillis();
        setColorPendulum1(SSPSimulationParameters.simParams.pendulumColor);
        setColorPendulum2(SSPSimulationParameters.simParams.pendulumColor2);
	}

	void restartRandom(double l, double m, double gr, double k, boolean trmd,
			int trLength) {
        frames = 0;
        fps = 0.f;
		g = SSPSimulationParameters.simParams.g;
		this.k = SSPSimulationParameters.simParams.k;
		this.gam = SSPSimulationParameters.simParams.gam;
		this.l = SSPSimulationParameters.simParams.l * 100.;
		this.aa = SSPSimulationParameters.simParams.aa * 100.;
		this.m1 = SSPSimulationParameters.simParams.m1;
		this.m2 = SSPSimulationParameters.simParams.m2;
		this.trmd = SSPSimulationParameters.simParams.showTrajectory;
		coordSystem = 0;
		float tr = (float)(aa + 0.5f*aa*(2.f*generator.nextDouble()-1.f));
		float tth = (float)(Math.acos(2.f*generator.nextDouble()-1.f));
		float tphi = (float) (2. * Math.PI * generator.nextDouble()); 
		q[0] = tr * Math.cos(tphi) * Math.sin(tth);
		q[1] = tr * Math.sin(tphi) * Math.sin(tth);
		q[2] = tr * Math.cos(tth);
		qv[0] = qv[1] = qv[2] = 0.;
		q[3] = (float) (Math.PI / 1.25 * generator.nextDouble()); // th
		q[4] = (float) (2. * Math.PI * generator.nextDouble()); // az
		qv[3] = (float) (0.5 * Math.PI * generator.nextDouble()); // thv
		qv[4] = (float) (Math.PI * generator.nextDouble()); // azv
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		qo[3] = q[3];
		qo[4] = q[4];
		mSphere = new SphereGL((float) (10. * Math.pow(m1, 1 / 3.)), 30, 30);
		mSphere2 = new SphereGL((float) (10. * Math.pow(m2, 1 / 3.)), 30, 30);
		mRod = new RodGL((float)(l-10.*Math.pow(m2, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m2, 1./3.)), 30);
		mSpring = new SpringGL((float)aa, 10);
		mTrajectory = new TrajectoryGL(SSPSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
		mTrajectory2 = new TrajectoryGL(SSPSimulationParameters.simParams.traceLength, (float)getx2(), (float)gety2(), (float)getz2(), 0.f, 0.f, 1.f);
		timeInterval = SystemClock.uptimeMillis();
	}

	protected void accel(double a[], double qt[], double qvt[]) {
		if (!dynamicGravity)
			accel1(a, qt, qvt);
		else
			accel2(a, qt, qvt);
	}

	void accel1(double a[], double qt[], double qvt[]) {
		double costh = Math.cos(qt[3]);
	    double sinth = Math.sin(qt[3]);
	    double cosph = Math.cos(qt[4]);
	    double sinph = Math.sin(qt[4]);
	    double coefs[][];
	    double bcoefs[];
	    coefs = new double[5][5];
	    if (coordSystem==0)
	    {
	        coefs[0][0] = (m1+m2);
	        coefs[0][1] = 0.;
	        coefs[0][2] = 0.;
	        coefs[0][3] = m2*l*cosph*costh;
	        coefs[0][4] = -m2*l*sinph*sinth;
	        coefs[1][0] = 0.;
	        coefs[1][1] = (m1+m2);
	        coefs[1][2] = 0.;
	        coefs[1][3] = m2*l*sinph*costh;
	        coefs[1][4] = m2*l*cosph*sinth;
	        coefs[2][0] = 0.;
	        coefs[2][1] = 0.;
	        coefs[2][2] = (m1+m2);
	        coefs[2][3] = -m2*l*sinth;
	        coefs[2][4] = 0.;
	        coefs[3][0] = m2*l*cosph*costh;
	        coefs[3][1] = m2*l*sinph*costh;
	        coefs[3][2] = -m2*l*sinth;
	        coefs[3][3] = m2*l*l;
	        coefs[3][4] = 0.;
	        coefs[4][0] = -m2*l*sinph*sinth;
	        coefs[4][1] = m2*l*cosph*sinth;
	        coefs[4][2] = 0.;
	        coefs[4][3] = 0.;
	        coefs[4][4] = m2*l*l*sinth*sinth;
	        bcoefs = new double[5];
	        bcoefs[0] = m2*l*(cosph*sinth*qvt[4]*qvt[4]+2*sinph*costh*qvt[3]*qvt[4]+cosph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[0] 
	                		- 2.*gam*qvt[0] - gam*l*(cosph*costh*qvt[3]-sinph*sinth*qvt[4]);
	        bcoefs[1] = m2*l*(sinph*sinth*qvt[4]*qvt[4]-2*cosph*costh*qvt[3]*qvt[4]+sinph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[1] 
	                		- 2.*gam*qvt[1] - gam*l*(sinph*costh*qvt[3]+cosph*sinth*qvt[4]);
	        bcoefs[2] = m2*l*costh*qvt[3]*qvt[3] + (m1+m2)*g
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[2] 
	                		- 2.*gam*qvt[2] + gam*l*sinth*qvt[3];
	        bcoefs[3] = m2*l*l*sinth*costh*qvt[4]*qvt[4] - m2*g*l*sinth
	        		- gam*qvt[0]*l*cosph*costh - gam*qvt[1]*l*sinph*costh + gam*qvt[2]*l*sinth
	        		- gam*l*l*qvt[3];
	        bcoefs[4] = -2.*m2*l*l*sinth*costh*qvt[3]*qvt[4]
	        		+ gam*qvt[0]*l*sinph*sinth - gam*qvt[1]*l*cosph*sinth - gam*l*l*qvt[4]*sinth*sinth;
	    }
	    else
	    {
	        coefs[0][0] = (m1+m2);
	        coefs[0][1] = 0.;
	        coefs[0][2] = 0.;
	        coefs[1][3] = m2*l*cosph*costh;
	        coefs[1][4] = -m2*l*sinph*sinth;
	        coefs[1][0] = 0.;
	        coefs[1][1] = (m1+m2);
	        coefs[1][2] = 0.;
	        coefs[2][3] = m2*l*sinph*costh;
	        coefs[2][4] = m2*l*cosph*sinth;
	        coefs[2][0] = 0.;
	        coefs[2][1] = 0.;
	        coefs[2][2] = (m1+m2);
	        coefs[0][3] = -m2*l*sinth;
	        coefs[0][4] = 0.;
	        coefs[3][1] = m2*l*cosph*costh;
	        coefs[3][2] = m2*l*sinph*costh;
	        coefs[3][0] = -m2*l*sinth;
	        coefs[3][3] = m2*l*l;
	        coefs[3][4] = 0.;
	        coefs[4][1] = -m2*l*sinph*sinth;
	        coefs[4][2] = m2*l*cosph*sinth;
	        coefs[4][0] = 0.;
	        coefs[4][3] = 0.;
	        coefs[4][4] = m2*l*l*sinth*sinth;
	        bcoefs = new double[5];
	        bcoefs[1] = m2*l*(cosph*sinth*qvt[4]*qvt[4]+2*sinph*costh*qvt[3]*qvt[4]+cosph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[1] 
	                		- 2.*gam*qvt[1] - gam*l*(cosph*costh*qvt[3]-sinph*sinth*qvt[4]);
	        bcoefs[2] = m2*l*(sinph*sinth*qvt[4]*qvt[4]-2*cosph*costh*qvt[3]*qvt[4]+sinph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[2] + (m1+m2)*g 
	                - 2.*gam*qvt[2] - gam*l*(sinph*costh*qvt[3]+cosph*sinth*qvt[4]);
	        bcoefs[0] = m2*l*costh*qvt[3]*qvt[3]
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[0] 
	                		- 2.*gam*qvt[0] + gam*l*sinth*qvt[3];
	        bcoefs[3] = m2*l*l*sinth*costh*qvt[4]*qvt[4] + m2*g*l*sinph*costh 
	        		+ gam*qvt[0]*l*sinth - gam*qvt[1]*l*cosph*costh - gam*qvt[2]*l*sinph*costh
	        		- gam*l*l*qvt[3];
	        bcoefs[4] = -2.*m2*l*l*sinth*costh*qvt[3]*qvt[4] + m2*g*l*cosph*sinth
	        		+ gam*qvt[1]*l*sinph*sinth - gam*qvt[2]*l*cosph*sinth - gam*l*l*qvt[4]*sinth*sinth;
	    }
		
	    LESolver syst;
	    syst = new LESolver(5, coefs, bcoefs);
	    double[] res = syst.Solve();
	    for(int i=0;i<5;++i) a[i] = res[i];
	}

	void accel2(double a[], double qt[], double qvt[]) {
		double costh = Math.cos(qt[3]);
	    double sinth = Math.sin(qt[3]);
	    double cosph = Math.cos(qt[4]);
	    double sinph = Math.sin(qt[4]);
	    double coefs[][];
	    double bcoefs[];
	    coefs = new double[5][5];
	    if (coordSystem==0)
	    {
	        coefs[0][0] = (m1+m2);
	        coefs[0][1] = 0.;
	        coefs[0][2] = 0.;
	        coefs[0][3] = m2*l*cosph*costh;
	        coefs[0][4] = -m2*l*sinph*sinth;
	        coefs[1][0] = 0.;
	        coefs[1][1] = (m1+m2);
	        coefs[1][2] = 0.;
	        coefs[1][3] = m2*l*sinph*costh;
	        coefs[1][4] = m2*l*cosph*sinth;
	        coefs[2][0] = 0.;
	        coefs[2][1] = 0.;
	        coefs[2][2] = (m1+m2);
	        coefs[2][3] = -m2*l*sinth;
	        coefs[2][4] = 0.;
	        coefs[3][0] = m2*l*cosph*costh;
	        coefs[3][1] = m2*l*sinph*costh;
	        coefs[3][2] = -m2*l*sinth;
	        coefs[3][3] = m2*l*l;
	        coefs[3][4] = 0.;
	        coefs[4][0] = -m2*l*sinph*sinth;
	        coefs[4][1] = m2*l*cosph*sinth;
	        coefs[4][2] = 0.;
	        coefs[4][3] = 0.;
	        coefs[4][4] = m2*l*l*sinth*sinth;
	        bcoefs = new double[5];
	        bcoefs[0] = m2*l*(cosph*sinth*qvt[4]*qvt[4]+2*sinph*costh*qvt[3]*qvt[4]+cosph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[0] + (m1+m2)*gx;
	        bcoefs[1] = m2*l*(sinph*sinth*qvt[4]*qvt[4]-2*cosph*costh*qvt[3]*qvt[4]+sinph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[1] + (m1+m2)*gy;
	        bcoefs[2] = m2*l*costh*qvt[3]*qvt[3] + (m1+m2)*gz
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[2];
	        bcoefs[3] = m2*l*l*sinth*costh*qvt[4]*qvt[4] +
	        		m2*l*(gx*cosph*costh+gy*sinph*costh-gz*sinth);
	        bcoefs[4] = -2.*m2*l*l*sinth*costh*qvt[3]*qvt[4]
	        		-m2*gx*l*sinph*sinth+m2*gy*l*cosph*sinth;
	    }
	    else
	    {
	        coefs[0][0] = (m1+m2);
	        coefs[0][1] = 0.;
	        coefs[0][2] = 0.;
	        coefs[1][3] = m2*l*cosph*costh;
	        coefs[1][4] = -m2*l*sinph*sinth;
	        coefs[1][0] = 0.;
	        coefs[1][1] = (m1+m2);
	        coefs[1][2] = 0.;
	        coefs[2][3] = m2*l*sinph*costh;
	        coefs[2][4] = m2*l*cosph*sinth;
	        coefs[2][0] = 0.;
	        coefs[2][1] = 0.;
	        coefs[2][2] = (m1+m2);
	        coefs[0][3] = -m2*l*sinth;
	        coefs[0][4] = 0.;
	        coefs[3][1] = m2*l*cosph*costh;
	        coefs[3][2] = m2*l*sinph*costh;
	        coefs[3][0] = -m2*l*sinth;
	        coefs[3][3] = m2*l*l;
	        coefs[3][4] = 0.;
	        coefs[4][1] = -m2*l*sinph*sinth;
	        coefs[4][2] = m2*l*cosph*sinth;
	        coefs[4][0] = 0.;
	        coefs[4][3] = 0.;
	        coefs[4][4] = m2*l*l*sinth*sinth;
	        bcoefs = new double[5];
	        bcoefs[1] = m2*l*(cosph*sinth*qvt[4]*qvt[4]+2*sinph*costh*qvt[3]*qvt[4]+cosph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[1] + (m1+m2)*gy;
	        bcoefs[2] = m2*l*(sinph*sinth*qvt[4]*qvt[4]-2*cosph*costh*qvt[3]*qvt[4]+sinph*sinth*qvt[3]*qvt[3])
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[2] + (m1+m2)*gz;
	        bcoefs[0] = m2*l*costh*qvt[3]*qvt[3]
	                -k*(1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2]))*qt[0] + (m1+m2)*gx;
	        bcoefs[3] = m2*l*l*sinth*costh*qvt[4]*qvt[4] +	
	        		m2*l*(gy*cosph*costh+gz*sinph*costh-gx*sinth);
	        bcoefs[4] = -2.*m2*l*l*sinth*costh*qvt[3]*qvt[4] 
	        			-m2*gy*l*sinph*sinth+m2*gz*l*cosph*sinth;
	    }
		
	    LESolver syst;
	    syst = new LESolver(5, coefs, bcoefs);
	    double[] res = syst.Solve();
	    for(int i=0;i<5;++i) a[i] = res[i];
	}

	void changeCoordinates() {
		int csyst = coordSystem;
	    csyst = (csyst+1)%2;
	    double tx2, ty2, tz2;
	    double txv2, tyv2, tzv2;
	    if (csyst==0)
	    {
	        tx2 = getx2() - q[0];
	        ty2 = gety2() - q[1];
	        tz2 = getz2() - q[2];
	        txv2 = getxv2() - qv[0];
	        tyv2 = getyv2() - qv[1];
	        tzv2 = getzv2() - qv[2];
	    }
	    else
	    {
	        tx2 = gety2() - q[1];
	        ty2 = getz2() - q[2];
	        tz2 = getx2() - q[0];
	        txv2 = getyv2() - qv[1];
	        tyv2 = getzv2() - qv[2];
	        tzv2 = getxv2() - qv[0];
	    }
	    double th2, ph2, thv2, phv2;
	    th2 = Math.acos(tz2 / l);
	    ph2 = Math.atan2(ty2, tx2);
	    thv2 = -tzv2 / l / Math.sin(th2);
	    phv2 = (tx2*tyv2 - ty2*txv2) / (tx2*tx2 + ty2*ty2);
	    q[3] = th2;
	    q[4] = ph2;
	    qv[3] = thv2;
	    qv[4] = phv2;
	    coordSystem = csyst;
	}

	double kien() {
		if (coordSystem==0) return (0.5*(m1+m2)*(qv[0]*qv[0] + qv[1]*qv[1] + qv[2]*qv[2])
                + 0.5*m2*l*l*(Math.sin(q[3])*Math.sin(q[3])*qv[4]*qv[4] + qv[3]*qv[3])
                + m2*l*((-Math.sin(q[4])*Math.sin(q[3])*qv[4]+Math.cos(q[4])*Math.cos(q[3])*qv[3])*qv[0]+
                        (Math.cos(q[4])*Math.sin(q[3])*qv[4]+Math.sin(q[4])*Math.cos(q[3])*qv[3])*qv[1]-
                        Math.sin(q[3])*qv[3]*qv[2]))/10000.0;
        else return (0.5*(m1+m2)*(qv[0]*qv[0] + qv[1]*qv[1] + qv[2]*qv[2])
                     + 0.5*m2*l*l*(Math.sin(q[3])*Math.sin(q[3])*qv[4]*qv[4] + qv[3]*qv[3])
                     + m2*l*((-Math.sin(q[4])*Math.sin(q[3])*qv[4]+Math.cos(q[4])*Math.cos(q[3])*qv[3])*qv[1]+
                             (Math.cos(q[4])*Math.sin(q[3])*qv[4]+Math.sin(q[4])*Math.cos(q[3])*qv[3])*qv[2]-
                             Math.sin(q[3])*qv[3]*qv[0]))/10000.0;
	}

	double poen() {
		if (coordSystem==0) return (-(m1+m2)*g*q[2] - m2*g*l*Math.cos(q[3]) + 0.5*k*(Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2])-aa)*(Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2])-aa))/10000.0;
        else return (-(m1+m2)*g*q[2] - m2*g*l*Math.sin(q[4])*Math.sin(q[3]) + 0.5*k*(Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2])-aa)*(Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2])-aa))/10000.0;
	}

	double ener() {
		return kien() + poen();
	}

	double getx1()
    {
        return q[0];
    }

    double getxv1()
    {
    	return qv[0];
    }

    double getxo1()
    {
    	return qo[0];
    }

    double getx2()
    {
        if (coordSystem==0) return q[0] + l*Math.cos(q[4])*Math.sin(q[3]);
        else return q[0] + l*Math.cos(q[3]);
    }

    double getxv2()
    {
        if (coordSystem==0) return qv[0] - l*Math.sin(q[4])*Math.sin(q[3])*qv[4] + l*Math.cos(q[4])*Math.cos(q[3])*qv[3];
        else return qv[0] - l*Math.sin(q[3])*qv[3];
    }

    double getxo2()
    {
        if (coordSystem==0) return qo[0] + l*Math.cos(qo[4])*Math.sin(qo[3]);
        else return qo[0] + l*Math.cos(qo[3]);
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
        if (coordSystem==0) return q[1] + l*Math.sin(q[4])*Math.sin(q[3]);
        else return q[1] + l*Math.cos(q[4])*Math.sin(q[3]);
    }

    double getyv2()
    {
        if (coordSystem==0) return qv[1] + l*Math.cos(q[4])*Math.sin(q[3])*qv[4] + l*Math.sin(q[4])*Math.cos(q[3])*qv[3];
        else return qv[1] - l*Math.sin(q[4])*Math.sin(q[3])*qv[4] + l*Math.cos(q[4])*Math.cos(q[3])*qv[3];
    }

    double getyo2()
    {
        if (coordSystem==0) return qo[1] + l*Math.sin(qo[4])*Math.sin(qo[3]);
        else return qo[1] + l*Math.cos(qo[4])*Math.sin(qo[3]);
    }

    double getz1()
    {
    	return q[2];
    }

    double getzv1()
    {
    	return qv[2];
    }

    double getzo1()
    {
    	return qo[2];
    }

    double getz2()
    {
        if (coordSystem==0) return q[2] + l*Math.cos(q[3]);
        else return q[2] + l*Math.sin(q[4])*Math.sin(q[3]);
    }

    double getzv2()
    {
        if (coordSystem==0) return qv[2] - l*Math.sin(q[3])*qv[3];
        return qv[2] + l*Math.cos(q[4])*Math.sin(q[3])*qv[4] + l*Math.sin(q[4])*Math.cos(q[3])*qv[3];
    }

    double getzo2()
    {
        if (coordSystem==0) return qo[2] + l*Math.cos(qo[3]);
        else return qo[2] + l*Math.sin(qo[4])*Math.sin(qo[3]);
    }

    protected void integrate(double dt, int pre) {
    	if (Math.abs(Math.cos(q[3]))>0.8) changeCoordinates();
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
		float factor = Height / 450.f;
		Matrix.translateM(MVMatrix, 0, 0.f, 10.f, -400.0f / zoomIn);
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
		if (elap < 0 || elap > 50)
			elap = 1;
		if (!paused) integrate(elap / 1000., 10);
	}

    public void draw(GL10 unused, int Width, int Height) {
		
		SSPGLRenderer.perspectiveGL(mProjMatrix, 45.0f,(float)(Width)/Height,0.1f,1200.0f);
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

		if (SSPSimulationParameters.simParams.showTrajectory && !SSPSimulationParameters.simParams.infiniteTrajectory)
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
		if (SSPSimulationParameters.simParams.showTrajectory && !SSPSimulationParameters.simParams.infiniteTrajectory)
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
		Matrix.scaleM(mVMatrix, 0, 1.f, 1.f, (float)Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2]));
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mSpring.draw(mProgram, mMVPMatrix, mVMatrix);
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 1);
		
		Matrix.scaleM(mVMatrix, 0, 1.f, 1.f, 1.f/(float)Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2]));
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
		//mRod2.draw(mProgram, mMVPMatrix, mVMatrix);
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
        if (enabled) this.gam = SSPSimulationParameters.simParams.gam;
        else this.gam = 0.;
    }

    public void setTraceMode(boolean enabled) {
        SSPSimulationParameters.simParams.showTrajectory = enabled;
    }
}
