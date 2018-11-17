package com.vlvolad.pendulumstudio.pendulumwave;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;


import com.vlvolad.pendulumstudio.common.GenericPendulum;
import com.vlvolad.pendulumstudio.common.RodGL;
import com.vlvolad.pendulumstudio.common.SphereGL;
import com.vlvolad.pendulumstudio.common.TrajectoryGL;
import com.vlvolad.pendulumstudio.mathematicalpendulum.MathematicalPendulum;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Volodymyr on 24.05.2015.
 */
public class PendulumWave extends GenericPendulum {
    public volatile boolean moved;
    double l, m, g, pp;
    public volatile double k;

    SphereGL mSphere;
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
    float scale;

    public volatile int NP, NT;
    public volatile double th0;
    public MathematicalPendulum[] Pendulums;

    public static Random generator = new Random();


    public PendulumWave(int NP, int NT, double l, double m, double th, double gr, double k, boolean trmd, int trLength,
                                boolean firsttime)
    {
        super();
        this.name = "Pendulum Wave Effect (3D)";
        g = gr;
        this.firsttime = firsttime;
        this.k = k;
        this.l = l;
        this.m = m;
        this.scale = 2.f;
        this.NT = NT;
        this.NP = NP;
        Pendulums = new MathematicalPendulum[NP];
        mSphere = new SphereGL((float) (10. * Math.pow(m, 1. / 3.)), 30, 30);
        mRod = new RodGL(1.f, 0.f, (float) (0.05f * 10.f * Math.pow(m, 1. / 3.)), 30);
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
        if (this.g<=0.001) this.g = 0.001;
        mTrajectory = new TrajectoryGL(trLength, 0.f, 0.f, 0.f);
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

        th0 = th;
        double T1 = 2. * Math.PI * Math.sqrt(this.l/this.g) / AGMean(1., Math.cos(th0/2.));
        for(int i=0;i<NP;++i) {
            double T2 = T1 * (double)(NT) / (NT+i);
            double Tl = T2 * AGMean(1., Math.cos(th0/2.)) / 2. / Math.PI;
            Tl = this.g * Tl * Tl;
            Pendulums[i] = new MathematicalPendulum(Tl * scale, this.m, th0, 0., this.g, this.k, trmd, trLength, true);
        }

        paused = false;
    }

    FloatBuffer fill3DVector(float x, float y, float z) {
        FloatBuffer buf;
        ByteBuffer vbb = ByteBuffer.allocateDirect(3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        buf = vbb.asFloatBuffer();
        buf.put(x);
        buf.put(y);
        buf.put(z);
        return buf;
    }

    FloatBuffer fill4DVector(float x, float y, float z, float w) {
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

    public double AGMean(double x, double y) {
        double am = (x + y) / 2.;
        double gm = Math.sqrt(x*y);
        while ((am-gm)/am>1e-8) {
            double amt = (am+gm)/2.;
            double gmt = Math.sqrt(am*gm);
            am = amt;
            gm = gmt;
        }
        return am;
    }

    public void restart() {
        frames = 0;
        fps = 0.f;
        firsttime = false;
        this.NP = PWSimulationParameters.simParams.NP;
        this.NT = PWSimulationParameters.simParams.NT;
        if (NP<1) NP = 1;
        if (NT<1) NT = 1;
        this.g = PWSimulationParameters.simParams.g;
        if (this.g<=0.001) this.g = 0.001;
        this.k = PWSimulationParameters.simParams.k;
        this.l = PWSimulationParameters.simParams.l * 100.;
        this.m = PWSimulationParameters.simParams.m;
        mSphere = new SphereGL((float) (10. * Math.pow(m, 1. / 3.)), 30, 30);
        mRod = new RodGL(1.f, 0.f, (float) (0.05f * 10.f * Math.pow(m, 1. / 3.)), 30);
        mTrajectory = new TrajectoryGL(10, 0.f, 0.f, 0.f);
        timeInterval = SystemClock.uptimeMillis();
        if (PWSimulationParameters.simParams.initRandom) th0 = (float)(Math.PI / 1.25 * generator.nextDouble());
        else th0 = PWSimulationParameters.simParams.th0;
        double T1 = 2. * Math.PI * Math.sqrt(this.l/this.g) / AGMean(1., Math.cos(th0/2.));
        Pendulums = new MathematicalPendulum[NP];
        for(int i=0;i<NP;++i) {
            double T2 = T1 * (double)(NT) / (NT+i);
            double Tl = T2 * AGMean(1., Math.cos(th0/2.)) / 2. / Math.PI;
            Tl = this.g * Tl * Tl;
            Pendulums[i] = new MathematicalPendulum(Tl, this.m, th0, 0., this.g, this.k, false, 100, true);
        }
    }

    public void restartPW(int set_np, int set_nt, double set_th0) {
        frames = 0;
        fps = 0.f;
        firsttime = false;
        this.NP = set_np;
        this.NT = set_nt;
        if (NP<1) NP = 1;
        if (NT<1) NT = 1;
        this.g = PWSimulationParameters.simParams.g;
        this.k = PWSimulationParameters.simParams.k;
        this.l = PWSimulationParameters.simParams.l * 100.;
        this.m = PWSimulationParameters.simParams.m;
        mSphere = new SphereGL((float) (10. * Math.pow(m, 1. / 3.)), 30, 30);
        mRod = new RodGL(1.f, 0.f, (float) (0.05f * 10.f * Math.pow(m, 1. / 3.)), 30);
        mTrajectory = new TrajectoryGL(10, 0.f, 0.f, 0.f);
        timeInterval = SystemClock.uptimeMillis();
        th0 = PWSimulationParameters.simParams.th0;//set_th0;
        double T1 = 2. * Math.PI * Math.sqrt(this.l/this.g) / AGMean(1., Math.cos(th0/2.));
        Pendulums = new MathematicalPendulum[NP];
        for(int i=0;i<NP;++i) {
            double T2 = T1 * (double)(NT) / (NT+i);
            double Tl = T2 * AGMean(1., Math.cos(th0/2.)) / 2. / Math.PI;
            Tl = this.g * Tl * Tl;
            Pendulums[i] = new MathematicalPendulum(Tl, this.m, th0, 0., this.g, this.k, false, 100, true);
        }
    }

    void restartRandom(double l, double m, double gr, double k, boolean trmd, int trLength) {
        frames = 0;
        fps = 0.f;
        this.NP = PWSimulationParameters.simParams.NP;
        this.NT = PWSimulationParameters.simParams.NT;
        if (NP<1) NP = 1;
        if (NT<1) NT = 1;
        this.g = gr;
        this.k = k;
        this.l = l;
        this.m = m;
        coordSystem = 0;
        mSphere = new SphereGL((float) (10. * m / 2.), 50, 50);
        mTrajectory = new TrajectoryGL(trLength, 0.f, 0.f, 0.f);
        mRod = new RodGL(1.f, 0.f, (float) (0.05f * 10.f * Math.pow(m, 1. / 3.)), 30);
        timeInterval = SystemClock.uptimeMillis();
        coordSystem = 0;
        th0 = (float)(Math.PI / 1.25 * generator.nextDouble());
        double T1 = 2. * Math.PI * Math.sqrt(this.l/this.g) / AGMean(1., Math.cos(th0/2.));
        Pendulums = new MathematicalPendulum[NP];
        for(int i=0;i<NP;++i) {
            double T2 = T1 * (double)(NT) / (NT+i);
            double Tl = T2 * AGMean(1., Math.cos(th0/2.)) / 2. / Math.PI;
            Tl = this.g * Tl * Tl;
            Pendulums[i] = new MathematicalPendulum(Tl, this.m, th0, 0., this.g, this.k, false, 100, true);
        }
    }

    public void setDamping(double k) {
        for(int i=0;i<NP;++i) Pendulums[i].k = k;
    }

    protected void integrate(double dt, int pre) {
        for(int i=0;i<NP;++i) Pendulums[i].integrate(dt, pre);
    }

    public void setCoord(float coordX, float coordY, float dx, float dy, int Width, int Height) {
    }

    protected void accel(double a[], double qt[], double qvt[]) {
    }

    public void preDraw() {
        if (firsttime) {
            restartPW(PWSimulationParameters.simParams.NP, PWSimulationParameters.simParams.NT, PWSimulationParameters.simParams.th0);
        }
        long elap = SystemClock.uptimeMillis() - timeInterval;
        timeInterval = SystemClock.uptimeMillis();
        if (elap < 0 || elap > 50) elap = 1;
        if (!moved && !paused) integrate(elap / 1000., 100);
    }

    public void draw(GL10 unused, int Width, int Height) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        PWGLRenderer.perspectiveGL(mProjMatrix, 45.0f,(float)(Width)/Height,0.1f,4200.0f);
        //MPGLRenderer.orthoGL(mProjMatrix, Width, Height);
        Matrix.setIdentityM(mVMatrix, 0);


        float factor = Height / 450.f;

        Matrix.translateM(mVMatrix, 0, 0.f, 0.f, -500.0f / zoomIn);
        Matrix.translateM(mVMatrix, 0, 0.f, 250.f*Height/3000.f, 0.f);
        Matrix.translateM(mVMatrix, 0, moveX, moveY, 0.f);
        Matrix.rotateM(mVMatrix, 0, 90.f, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mVMatrix, 0, -90.f, 0.0f, 0.0f, 1.0f);

        for(int i=0;i<NP;++i) {

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            Matrix.translateM(mVMatrix, 0, 30.f * i, 0.f, 0.f);

            float x, y, z;
            x = (float) Pendulums[i].getx() * scale;//(float) (l*Math.sin(q[0])*Math.cos(q[1]));
            y = (float) Pendulums[i].gety() * scale;//(float) (l*Math.sin(q[0])*Math.sin(q[1]));
            z = (float) Pendulums[i].getz() * scale;//(float) (l*Math.cos(q[0]));
            lineVertexBuffer.put(3, x);
            lineVertexBuffer.put(4, y);
            lineVertexBuffer.put(5, z);
            lineVertexBuffer.position(0);

            int tHandle = GLES20.glGetUniformLocation(mProgram, "color");
            GLES20.glUniform4f(tHandle, Color1R / 255.f, Color1G / 255.f, Color1B / 255.f, 1.0f);
            //GLES20.glUniform4f(tHandle, 0.0f, 0.0f, 0.0f, 1.0f);
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

            Matrix.rotateM(mVMatrix, 0, (float) (180.f / Math.PI * Math.atan2(y, x)), 0.f, 0.f, 1.f);
            Matrix.rotateM(mVMatrix, 0, (float) (180.f / Math.PI * Math.acos(z / (float)Math.sqrt(x * x + y * y + z * z))), 0.f, 1.f, 0.f);
            Matrix.scaleM(mVMatrix, 0, 1.0f, 1.0f, (scale*(float)(Pendulums[i].l)-10.f * (float)Math.pow(m, 1. / 3.)));
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
            mRod.draw(mProgram, mMVPMatrix, mVMatrix);
            Matrix.scaleM(mVMatrix, 0, 1.0f, 1.0f, 1.f/(scale*(float)(Pendulums[i].l)-10.f * (float)Math.pow(m, 1. / 3.)));

            tHandle = GLES20.glGetUniformLocation(mProgram, "color");
            GLES20.glUniform4f(tHandle, Color1R / 255.f, Color1G / 255.f, Color1B / 255.f, 1.0f);

            Matrix.rotateM(mVMatrix, 0, (float) (180.f / Math.PI * Math.acos(z / (float)Math.sqrt(x * x + y * y + z * z))), 0.f, -1.f, 0.f);
            Matrix.rotateM(mVMatrix, 0, (float) (180.f / Math.PI * Math.atan2(y, x)), 0.f, 0.f, -1.f);


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


            Matrix.translateM(mVMatrix, 0, -x, -y, -z);
            Matrix.rotateM(mVMatrix, 0, (float) (-180.f / Math.PI * Math.atan2(y, x)), 0.f, 0.f, -1.f);
            Matrix.rotateM(mVMatrix, 0, (float) (-180.f / Math.PI * Math.acos(z / (float)Math.sqrt(x * x + y * y + z * z))), 0.f, -1.f, 0.f);

            Matrix.rotateM(mVMatrix, 0, (float) (-180.f / Math.PI * Math.acos(z / (float)Math.sqrt(x * x + y * y + z * z))), 0.f, 1.f, 0.f);
            Matrix.rotateM(mVMatrix, 0, (float) (-180.f / Math.PI * Math.atan2(y, x)), 0.f, 0.f, 1.f);

            Matrix.translateM(mVMatrix, 0, -30.f * i, 0.f, 0.f);
        }

        if (NP>1) {
            Matrix.rotateM(mVMatrix, 0, 90.f, 0.0f, 0.0f, 1.0f);
            Matrix.rotateM(mVMatrix, 0, 90.f, 1.0f, 0.0f, 0.0f);
            Matrix.scaleM(mVMatrix, 0, 1.0f, 1.0f, 30.f*(NP-1));
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
            mRod.draw(mProgram, mMVPMatrix, mVMatrix);
            Matrix.scaleM(mVMatrix, 0, 1.0f, 1.0f, 1.f/(30.f*(NP-1)));
        }
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
        //mTrajectory.clearTrajectory((float)getx(), (float)gety(), (float)getz());
    }

    public void setDampingMode(boolean enabled) {
        if (enabled) {
            this.k = PWSimulationParameters.simParams.k;
            setDamping(PWSimulationParameters.simParams.k);
        }
        else {
            this.k = 0.;
            setDamping(0.);
        }

    }

    public void setTraceMode(boolean enabled) {
    }
}
