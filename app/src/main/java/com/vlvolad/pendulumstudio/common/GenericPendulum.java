package com.vlvolad.pendulumstudio.common;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Volodymyr on 06.04.2015.
 */

/**
 * Class for a generic system consisting of 1 to 2 pendulums
 * Inherits MechSystemrkf45 for a generic mechanical system
 * with Newton's equations of motions solved numrically using
 * the Runge-Kutta-Fehlberg's scheme with adaptive time step
 */
public abstract class GenericPendulum extends MechSystemrkf45 {
    public volatile boolean firsttime;
    public volatile float gx, gy, gz;
    public volatile boolean dynamicGravity;
    public volatile boolean paused;

    public volatile int Color1R, Color1G, Color1B;
    public volatile int Color2R, Color2G, Color2B;

    public TrajectoryGL mTrajectory, mTrajectory2;

    public String name;

    public int mProgram;

    public volatile float fps;
    public volatile int frames;

    public final float[] mMVPMatrix = new float[16];
    public final float[] mProjMatrix = new float[16];
    public final float[] mVMatrix = new float[16];

    public abstract void preDraw();

    public abstract void draw(GL10 unused, int Width, int Height);
    public abstract void restart();


    public void toggleGravity()
    {
        dynamicGravity = !dynamicGravity;
    }

    public void setGravityMode(boolean useDynamic)
    {
        dynamicGravity = useDynamic;
    }

    public void setGravity(float ggx, float ggy, float ggz)
    {
        gx = 100.f * (ggz);
        gy = 100.f * (-ggx);
        gz = 100.f * (ggy);
    }

    public void setColorPendulum1(int color) {
        int b = (color)&0xFF;
        int g = (color>>8)&0xFF;
        int r = (color>>16)&0xFF;
        int a = (color>>24)&0xFF;
        Color1B = b;
        Color1G = g;
        Color1R = r;

        if (mTrajectory!=null) mTrajectory.setColor(r/255.f, g/255.f, b/255.f);
    }

    public void setColorPendulum2(int color) {
        int b = (color)&0xFF;
        int g = (color>>8)&0xFF;
        int r = (color>>16)&0xFF;
        int a = (color>>24)&0xFF;
        Color2B = b;
        Color2G = g;
        Color2R = r;

        if (mTrajectory2!=null) mTrajectory2.setColor(r/255.f, g/255.f, b/255.f);
    }

    public GenericPendulum() {
        Color1R = 255;
        Color1G = Color1B = 0;
        Color2B = 255;
        Color2G = Color2R = 0;
        mTrajectory = null;
        mTrajectory2 = null;
        fps = 0.f;
        frames = 0;
    }

    public abstract void setDampingMode(boolean enabled);
    public abstract void setTraceMode(boolean enabled);
}
