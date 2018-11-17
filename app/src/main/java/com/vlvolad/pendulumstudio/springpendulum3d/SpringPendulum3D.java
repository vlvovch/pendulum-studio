package com.vlvolad.pendulumstudio.springpendulum3d;

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
import com.vlvolad.pendulumstudio.common.SpringGL;
import com.vlvolad.pendulumstudio.common.TrajectoryGL;

public class SpringPendulum3D extends GenericPendulum {
	double aa, m, g, k, ken, pen, pp;
	public volatile double gam;
    public volatile boolean moved;
	double qo[];
	boolean trmd;
	SphereGL mSphere;
	SpringGL mSpring;
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

	public static Random generator = new Random();

    public SpringPendulum3D(double aa, double k, double m,
			double x, double xv,
			double y, double yv,
			double z, double zv,
			double gr, double gam,
			boolean trmd, int trLength, boolean firsttime)
	{
		super();
        this.name = "Spring Pendulum (3D)";
		this.firsttime = firsttime;
		g = gr;
		this.k = k;
		this.aa = aa;
		this.m = m;
		this.gam = gam;
		this.trmd = trmd;
		sz = 3;
		q = new double[sz];
		qv = new double[sz];
		qo = new double[sz];
		q[0] = x;
		qv[0] = xv;
		qo[0] = q[0];
		q[1] = y;
		qv[1] = yv;
		qo[1] = q[1];
		q[2] = z;
		qv[2] = zv;
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
		mSphere = new SphereGL((float) (10. * Math.pow(m, 1 / 3.)), 30, 30);
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
		zoomIn = 1.f;
		moveX = 0.f;
		moveY = 0.f;
		dynamicGravity = false;
		gy = gx = 0.f;
		gz = 981.f;
		mTrajectory = new TrajectoryGL(trLength, (float)getx1(), (float)gety1(), (float)getz1());
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
		g = SP3DSimulationParameters.simParams.g;
		this.k = SP3DSimulationParameters.simParams.k;
		this.aa = SP3DSimulationParameters.simParams.aa * 100.;
		this.m = SP3DSimulationParameters.simParams.m;
		this.gam = SP3DSimulationParameters.simParams.gam;
		this.trmd = SP3DSimulationParameters.simParams.showTrajectory;
		if (SP3DSimulationParameters.simParams.initRandom) {
			float tr = (float)(aa + 0.5f*aa*(2.f*generator.nextDouble()-1.f));
			float tth = (float)(Math.acos(0.999999 * (2.f*generator.nextDouble()-1.f)));
			float tphi = (float) (2. * Math.PI * generator.nextDouble()); 
			q[0] = tr * Math.cos(tphi) * Math.sin(tth);
			q[1] = tr * Math.sin(tphi) * Math.sin(tth);
			q[2] = tr * Math.cos(tth);
			qv[0] = qv[1] = qv[2] = 0.;
		} else {
			q[0] = SP3DSimulationParameters.simParams.x * 100.;
			q[1] = SP3DSimulationParameters.simParams.y * 100.;
			q[2] = SP3DSimulationParameters.simParams.z * 100.;
			qv[0] = SP3DSimulationParameters.simParams.xv * 100.;
			qv[1] = SP3DSimulationParameters.simParams.yv * 100.;
			qv[2] = SP3DSimulationParameters.simParams.zv * 100.;
		}
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		mSphere = new SphereGL((float) (10. * Math.pow(m, 1 / 3.)), 30, 30);
		mTrajectory = new TrajectoryGL(SP3DSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
        mSpring = new SpringGL((float)aa, 10);
		timeInterval = SystemClock.uptimeMillis();
        setColorPendulum1(SP3DSimulationParameters.simParams.pendulumColor);
	}

	void restartRandom(double l, double m, double gr, double k, boolean trmd,
			int trLength) {
        frames = 0;
        fps = 0.f;
		g = gr;
		g = SP3DSimulationParameters.simParams.g;
		this.k = SP3DSimulationParameters.simParams.k;
		this.aa = SP3DSimulationParameters.simParams.aa * 100.;
		this.m = SP3DSimulationParameters.simParams.m;
		this.gam = SP3DSimulationParameters.simParams.gam;
		this.trmd = SP3DSimulationParameters.simParams.showTrajectory;
		float tr = (float)(aa + 0.5f*aa*(2.f*generator.nextDouble()-1.f));
		float tth = (float)(Math.acos(2.f*generator.nextDouble()-1.f));
		float tphi = (float) (2. * Math.PI * generator.nextDouble()); 
		q[0] = tr * Math.cos(tphi) * Math.sin(tth);
		q[1] = tr * Math.sin(tphi) * Math.sin(tth);
		q[2] = tr * Math.cos(tth);
		qv[0] = qv[1] = qv[2] = 0.;
		qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		mSphere = new SphereGL((float) (10. * Math.pow(m, 1 / 3.)), 30, 30);
        mTrajectory = new TrajectoryGL(SP3DSimulationParameters.simParams.traceLength, (float)getx1(), (float)gety1(), (float)getz1());
        timeInterval = SystemClock.uptimeMillis();
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
		 a[0] = - k/m * (1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2])) * qt[0] - gam / m * qvt[0];
		 a[1] = - k/m * (1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2])) * qt[1] - gam / m * qvt[1];
		 a[2] = - k/m * (1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2])) * qt[2] - gam / m * qvt[2]
				 + g;
	}

	void accel2(double a[], double qt[], double qvt[]) {
		 a[0] = - k/m * (1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2])) * qt[0] - gam / m * qvt[0]
				 + gx;
		 a[1] = - k/m * (1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2])) * qt[1] - gam / m * qvt[1]
				 + gy;
		 a[2] = - k/m * (1.-aa/Math.sqrt(qt[0]*qt[0]+qt[1]*qt[1]+qt[2]*qt[2])) * qt[2] - gam / m * qvt[2]
				 + gz;
	}

	double kien() {
		return (0.5*m*(qv[0]*qv[0]+qv[1]*qv[1]+qv[2]*qv[2]))/10000.0;
	}

	double poen() {
		return (0.5*k*(Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2])-aa)*(Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2])-aa) 
				+ m*g*q[2])/10000.0;
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

    protected void integrate(double dt, int pre) {
        qo[0] = q[0];
		qo[1] = q[1];
		qo[2] = q[2];
		super.integrate(dt, pre);
		mTrajectory.addPointToTrajectory((float)getx1(), (float)gety1(), (float)getz1());
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
		
		SP3DGLRenderer.perspectiveGL(mProjMatrix, 45.0f,(float)(Width)/Height,0.1f,1200.0f);
        //SP3DGLRenderer.perspectiveGL(mProjMatrix, 80.0f,(float)(Width)/Height,0.1f,1200.0f);
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

		if (SP3DSimulationParameters.simParams.showTrajectory && !SP3DSimulationParameters.simParams.infiniteTrajectory)
			mTrajectory.draw(mProgram, mMVPMatrix);

		tHandle = GLES20.glGetUniformLocation(mProgram, "color");
		GLES20.glUniform4f(tHandle, 0.0f, 0.0f, 1.0f, 1.0f);
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, lineVertexBuffer);
		
		
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
		Matrix.scaleM(mVMatrix, 0, 1.f, 1.f, (float)Math.sqrt(x*x+y*y+z*z));
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mSpring.draw(mProgram, mMVPMatrix, mVMatrix);
		tHandle = GLES20.glGetUniformLocation(mProgram, "light");
		GLES20.glUniform1i(tHandle, 1);
		
		Matrix.scaleM(mVMatrix, 0, 1.f, 1.f, 1.f/(float)Math.sqrt(x*x+y*y+z*z));
        if (Math.atan2(y, x)>0.) Matrix.rotateM(mVMatrix, 0, (float)(180.f), 0.f, 0.f, -1.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.acos(z/(float)Math.sqrt(x*x+y*y+z*z))), 0.f, -1.f, 0.f);
		Matrix.rotateM(mVMatrix, 0, (float)(180.f/Math.PI * Math.atan2(y, x)), 0.f, 0.f, -1.f);
		
		Matrix.translateM(mVMatrix, 0, x, y, z);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mSphere.draw(mProgram, mMVPMatrix, mVMatrix);

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
    }

    public void setDampingMode(boolean enabled) {
        if (enabled) this.gam = SP3DSimulationParameters.simParams.gam;
        else this.gam = 0.;
    }

    public void setTraceMode(boolean enabled) {
        SP3DSimulationParameters.simParams.showTrajectory = enabled;
    }
}
