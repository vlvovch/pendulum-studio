package com.vlvolad.pendulumstudio.sphericalpendulum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;


import com.vlvolad.pendulumstudio.common.MechSystemrkf45;
import com.vlvolad.pendulumstudio.common.GenericPendulum;
import com.vlvolad.pendulumstudio.common.SphereGL;
import com.vlvolad.pendulumstudio.common.RodGL;
import com.vlvolad.pendulumstudio.common.TrajectoryGL;
import com.vlvolad.pendulumstudio.mathematicalpendulum.MPSimulationParameters;

public class SphericalPendulum extends GenericPendulum{
	double l,m,g,ken,pen,pp;
	public volatile double k;
	double qo[];
	boolean trmd;
	SphereGL mSphere;
	RodGL mRod;
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
	
	public static Random  generator = new Random();
    public SphericalPendulum(double l, double m, double th, double az, double thv, double azv, double gr, double k, boolean trmd, int trLength,
			boolean firsttime)
	//	: g(gr), k(k)
	{
		super();
        this.name = "Spherical Pendulum (3D)";
		g = gr;
		this.firsttime = firsttime;
		this.k = k;
		this.l = l;
		this.m = m;
		this.trmd = trmd;
		sz = 2;
		q = new double[sz];
		qv = new double[sz];
		qo = new double[sz];
		q[0] = th;
		q[1] = az;
		qv[0] = thv;
		qv[1] = azv;
		qo[0] = q[0];
		qo[1] = q[1];
		qt = new double[sz];
		qvt = new double[sz];
		a = new double[sz];
		k1 = new double[2*sz];
		k2 = new double[2*sz];
		k3 = new double[2*sz];
		k4 = new double[2*sz];
		k5 = new double[2*sz];
		k6 = new double[2*sz];
		//k = 1e-6;
		pp = qv[1]*m*l*l*Math.sin(q[0])*Math.sin(q[0]);
		mSphere = new SphereGL((float)(10.*Math.pow(m, 1./3.)), 30, 30);
		mRod = new RodGL((float)(l-10.*Math.pow(m, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m, 1./3.)), 30);
		ByteBuffer vbb = ByteBuffer.allocateDirect(6 * 4);
        vbb.order(ByteOrder.nativeOrder());
		lineVertexBuffer = vbb.asFloatBuffer();
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		lineVertexBuffer.put(0.f);
		timeInterval = SystemClock.uptimeMillis();
		zoomIn = 1.f;
		moveX = 0.f;
		moveY = 0.f;
		dynamicGravity = false;
		gy = gx = 0.f;
		gz = 981.f;
		mTrajectory = new TrajectoryGL(trLength, (float)getx(), (float)gety(), (float)getz());
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

        paused = false;
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
	
	public void restart()
	{
        frames = 0;
        fps = 0.f;
		firsttime = false;
		g = SPSimulationParameters.simParams.g;
		this.k = SPSimulationParameters.simParams.k;
		this.l = SPSimulationParameters.simParams.l * 100.;
		this.m = SPSimulationParameters.simParams.m;
		this.trmd = SPSimulationParameters.simParams.showTrajectory;
		if (SPSimulationParameters.simParams.initRandom)
		{
            q[0] = (float) Math.acos(0.999999 * (2. * generator.nextDouble() - 1.));
			q[1] = (float)(2. * Math.PI  * generator.nextDouble()); 		//az
			qv[0] = (float)(0.5 * Math.PI  * generator.nextDouble());		//thv
			qv[1] = (float)(Math.PI  * generator.nextDouble());				//azv
		}
		else
		{
			q[0] = SPSimulationParameters.simParams.th0;
			q[1] = SPSimulationParameters.simParams.ph0;
			qv[0] = SPSimulationParameters.simParams.thv0;
			qv[1] = SPSimulationParameters.simParams.phv0;
		}
		coordSystem = 0;
		qo[0] = q[0];
		qo[1] = q[1];
		pp = qv[1]*m*l*l*Math.sin(q[0])*Math.sin(q[0]);
		mSphere = new SphereGL((float)(10.*Math.pow(m, 1./3.)), 30, 30);
		mRod = new RodGL((float)(l-10.*Math.pow(m, 1./3.)), 0.f, (float)(0.1f*10.f*Math.pow(m, 1./3.)), 30);
		mTrajectory = new TrajectoryGL(SPSimulationParameters.simParams.traceLength, (float)getx(), (float)gety(), (float)getz());
		timeInterval = SystemClock.uptimeMillis();
        setColorPendulum1(SPSimulationParameters.simParams.pendulumColor);
	}
	
	void restartRandom(double l, double m, double gr, double k, boolean trmd, int trLength)
	{
        frames = 0;
        fps = 0.f;
		g = gr;
		this.k = k;
		this.l = l;
		this.m = m;
		this.trmd = trmd;
		coordSystem = 0;
		q[0] = (float)(Math.PI / 1.25 * generator.nextDouble()); 		//th
		q[1] = (float)(2. * Math.PI  * generator.nextDouble()); 		//az
		qv[0] = (float)(0.5 * Math.PI  * generator.nextDouble());		//thv
		qv[1] = (float)(Math.PI  * generator.nextDouble());				//azv
		qo[0] = q[0];
		qo[1] = q[1];
		pp = qv[1]*m*l*l*Math.sin(q[0])*Math.sin(q[0]);
		mSphere = new SphereGL((float)(10.*m/2.), 50, 50);
		mTrajectory = new TrajectoryGL(trLength, (float)getx(), (float)gety(), (float)getz());
		timeInterval = SystemClock.uptimeMillis();
		coordSystem = 0;
	}
	
	
	protected void accel(double a[], double qt[], double qvt[])
	{
		if (!dynamicGravity) accel1(a,qt,qvt);
		else accel2(a, qt, qvt);
	}
	
	void accel1(double a[], double qt[], double qvt[])
	{
		if (coordSystem==0)
		{
			a[0] = - g*Math.sin(qt[0])/l + 0.5*Math.sin(2*qt[0])*qvt[1]*qvt[1] - k*qvt[0]/m;
			a[1] = - 2*Math.cos(qt[0])*qvt[0]*qvt[1]/Math.sin(qt[0]) - k*qvt[1]/m;
		}
		else
		{
			a[0] = g*Math.cos(qt[1])*Math.cos(qt[0])/l + 0.5*Math.sin(2*qt[0])*qvt[1]*qvt[1] - k*qvt[0]/m;
			a[1] = -g*Math.sin(qt[1])/Math.sin(qt[0])/l - 2*Math.cos(qt[0])*qvt[0]*qvt[1]/Math.sin(qt[0]) - k*qvt[1]/m;
		}
	}
	
	void accel2(double a[], double qt[], double qvt[])
	{
		a[0] = gx*Math.cos(qt[1])*Math.cos(qt[0])/l + gy*Math.sin(qt[1])*Math.cos(qt[0])/l - gz*Math.sin(qt[0])/l + 0.5*Math.sin(2*qt[0])*qvt[1]*qvt[1] - k*qvt[0]/m;
		a[1] = (-gx*Math.sin(qt[1])/l + gy*Math.cos(qt[1])/l - 2*Math.cos(qt[0])*qvt[0]*qvt[1])/Math.sin(qt[0]) - k*qvt[1]/m;
		if (coordSystem==0)
		{
			a[0] = gx*Math.cos(qt[1])*Math.cos(qt[0])/l + gy*Math.sin(qt[1])*Math.cos(qt[0])/l - gz*Math.sin(qt[0])/l + 0.5*Math.sin(2*qt[0])*qvt[1]*qvt[1] - k*qvt[0]/m;
			a[1] = (-gx*Math.sin(qt[1])/l + gy*Math.cos(qt[1])/l - 2*Math.cos(qt[0])*qvt[0]*qvt[1])/Math.sin(qt[0]) - k*qvt[1]/m;
		}
		else
		{
			a[0] = gz*Math.cos(qt[1])*Math.cos(qt[0])/l + gx*Math.sin(qt[1])*Math.cos(qt[0])/l - gy*Math.sin(qt[0])/l + 0.5*Math.sin(2*qt[0])*qvt[1]*qvt[1] - k*qvt[0]/m;
			a[1] = (-gz*Math.sin(qt[1])/l + gx*Math.cos(qt[1])/l - 2*Math.cos(qt[0])*qvt[0]*qvt[1])/Math.sin(qt[0]) - k*qvt[1]/m;
		}
	}
	
	void changeCoordinates()
	{
		int csyst = coordSystem;
	    csyst = (csyst+1)%2;
	    double tx, ty, tz;
        double txv, tyv, tzv;
        if (csyst==0)
        {
            tx = getx();
            ty = gety();
            tz = getz();
            txv = getxv();
            tyv = getyv();
            tzv = getzv();
        }
        else
        {
            tx = getz();
            ty = getx();
            tz = gety();
            txv = getzv();
            tyv = getxv();
            tzv = getyv();
        }
        double th, ph, thv, phv;
        th = Math.acos(tz / l);
        ph = Math.atan2(ty, tx);
        thv = -tzv / l / Math.sin(th);
        phv = (tx*tyv - ty*txv) / (tx*tx + ty*ty);
        q[0] = th;
        q[1] = ph;
        qv[0] = thv;
        qv[1] = phv;
        coordSystem = csyst;
	}
	
	double kien()
	{
		return (0.5*m*(l*l*Math.sin(q[0])*Math.sin(q[0])*qv[1]*qv[1] + l*l*qv[0]*qv[0]))/10000.0;
	}

	double poen()
	{
		if (coordSystem==0) return (- m*g*l*Math.cos(q[0]))/10000.0;
		else return (-m*g*l*Math.cos(q[1])*Math.sin(q[0]))/10000.0;
	}

	double ener()
	{
		return kien() + poen();
	}
	
	double getx()
    {
        if (coordSystem==0) return l * Math.cos(q[1]) * Math.sin(q[0]);
        else return l * Math.sin(q[1]) * Math.sin(q[0]);
    }
	
	double getxv()
    {
        if (coordSystem==0) return -l*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l*Math.cos(q[1])*Math.cos(q[0])*qv[0];
        else return l*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l*Math.sin(q[1])*Math.cos(q[0])*qv[0];
    }

    double getxo()
    {
    	if (coordSystem==0) return l * Math.cos(qo[1]) * Math.sin(qo[0]);
        else return l * Math.sin(qo[1]) * Math.sin(qo[0]);
    }

    double gety()
    {
    	if (coordSystem==0) return l * Math.sin(q[1]) * Math.sin(q[0]);
        else return l * Math.cos(q[0]);
    }
    
    double getyv()
    {
        if (coordSystem==0) return l*Math.cos(q[1])*Math.sin(q[0])*qv[1] + l*Math.sin(q[1])*Math.cos(q[0])*qv[0];
        else return -l*Math.sin(q[0])*qv[0];
    }

    double getyo()
    {
    	if (coordSystem==0) return l * Math.sin(qo[1]) * Math.sin(qo[0]);
        else return l * Math.cos(qo[0]);
    }

    double getz()
    {
    	if (coordSystem==0) return l * Math.cos(q[0]);
        else return l * Math.cos(q[1]) * Math.sin(q[0]);
    }
    
    double getzv()
    {
    	if (coordSystem==0) return -l*Math.sin(q[0])*qv[0];
        else return -l*Math.sin(q[1])*Math.sin(q[0])*qv[1] + l*Math.cos(q[1])*Math.cos(q[0])*qv[0];
    }

    double getzo()
    {
    	if (coordSystem==0) return l * Math.cos(qo[0]);
        else return l * Math.cos(qo[1]) * Math.sin(qo[0]);
    }
	
	protected void integrate(double dt, int pre)
	{
		if (Math.abs(Math.cos(q[0]))>0.8) changeCoordinates();
		qo[0] = q[0];
		qo[1] = q[1];
		super.integrate(dt, pre);
		mTrajectory.addPointToTrajectory((float)getx(), (float)gety(), (float)getz());
	}

	public void translateMVMatrix(float[] MVMatrix, int Width, int Height) {
        Matrix.setIdentityM(MVMatrix, 0);
        Matrix.translateM(MVMatrix, 0, 0.f, 0.f, -400.0f / zoomIn);
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
		if (elap<0 || elap>50) elap = 1;
		if (!paused) integrate(elap/1000., 100);
	}

    public void draw(GL10 unused, int Width, int Height)
	{
	    SPGLRenderer.perspectiveGL(mProjMatrix, 45.0f, (float)(Width)/Height,0.1f,1200.0f);
	    Matrix.setIdentityM(mVMatrix, 0);
		
	    GLES20.glEnable(GLES20.GL_DEPTH_TEST);

	    Matrix.translateM(mVMatrix, 0, 0.f, 0.f, -400.0f / zoomIn);
	    Matrix.translateM(mVMatrix, 0, 0.f, 30.f*Height/3000.f, 0.f);
	    Matrix.translateM(mVMatrix, 0, moveX, moveY, 0.f);
	    Matrix.rotateM(mVMatrix, 0, 90.f, 1.0f, 0.0f, 0.0f);
	    Matrix.rotateM(mVMatrix, 0, -90.f, 0.0f, 0.0f, 1.0f);


	    float x,y,z;
	    x = (float)getx();//(float) (l*Math.sin(q[0])*Math.cos(q[1]));
	    y = (float)gety();//(float) (l*Math.sin(q[0])*Math.sin(q[1]));
	    z = (float)getz();//(float) (l*Math.cos(q[0]));
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


		if (SPSimulationParameters.simParams.showTrajectory && !SPSimulationParameters.simParams.infiniteTrajectory)
		    mTrajectory.draw(mProgram, mMVPMatrix);

    	
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.atan2(y, x)), 0.f, 0.f, 1.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.acos(z/(float)Math.sqrt(x*x+y*y+z*z))), 0.f, 1.f, 0.f);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mRod.draw(mProgram, mMVPMatrix, mVMatrix);
		
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.acos(z/(float)Math.sqrt(x*x+y*y+z*z))), 0.f, -1.f, 0.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.atan2(y, x)), 0.f, 0.f, -1.f);
    	
    	
    	tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 1);
		
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
		
		Matrix.translateM(mVMatrix, 0, x, y, z);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
	    mSphere.draw(mProgram, mMVPMatrix, mVMatrix);

	    tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 0);
	    GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	}

    public void clearTrajectory() {
        mTrajectory.clearTrajectory((float)getx(), (float)gety(), (float)getz());
    }

    public void setDampingMode(boolean enabled) {
        if (enabled) this.k = SPSimulationParameters.simParams.k;
        else this.k = 0.;
    }

    public void setTraceMode(boolean enabled) {
        SPSimulationParameters.simParams.showTrajectory = enabled;
    }

}
