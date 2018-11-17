package com.vlvolad.pendulumstudio.livewallpaper;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.vlvolad.pendulumstudio.common.GenericPendulum;
import com.vlvolad.pendulumstudio.doublependulum.DoublePendulum;
import com.vlvolad.pendulumstudio.doublesphericalpendulum.DoubleSphericalPendulum;
import com.vlvolad.pendulumstudio.mathematicalpendulum.MathematicalPendulum;
import com.vlvolad.pendulumstudio.pendulumwave.PendulumWave;
import com.vlvolad.pendulumstudio.sphericalpendulum.SphericalPendulum;
import com.vlvolad.pendulumstudio.springmathematicalpendulum.SpringMathematicalPendulum;
import com.vlvolad.pendulumstudio.springpendulum2d.SpringPendulum2D;
import com.vlvolad.pendulumstudio.springpendulum3d.SpringPendulum3D;
import com.vlvolad.pendulumstudio.springsphericalpendulum.SpringSphericalPendulum;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Volodymyr on 06.04.2015.
 */
public class PendulumRenderer implements GLSurfaceView.Renderer{
    private static final String TAG = "PendulumRenderer";
    public volatile GenericPendulum mPendulum;
    public static DoublePendulum mPendulumDP = new DoublePendulum(75., 75., 1., 1., 45. * Math.PI / 180.,
            5. * Math.PI / 8., 0. * Math.PI / 180.,
            45. * Math.PI / 180., 9.81 * 100., 0. /1.e6, true, 100, true);
    public static DoubleSphericalPendulum mPendulumDSP = new DoubleSphericalPendulum(75., 75., 1., 1., 45. * Math.PI / 180., 90. * Math.PI / 180.,
            5. * Math.PI / 8., 90. * Math.PI / 180., 0. * Math.PI / 180.,
            0.* Math.PI / 180., 45. * Math.PI / 180., 45. * Math.PI / 180., 9.81 * 100., 0. /1.e6, true, 100, true);
    public static MathematicalPendulum mPendulumMP = new MathematicalPendulum(100., 1., 90. * Math.PI / 180.,
            0.* Math.PI / 180., 9.81 * 100., 0. /1.e6, true, 3000, true);
    public static SphericalPendulum mPendulumSP = new SphericalPendulum(100., 1., 45. * Math.PI / 180., 90. * Math.PI / 180.,
            0. * Math.PI / 180., 0.* Math.PI / 180., 9.81 * 100., 0. /1.e6, true, 3000, true);
    public static SpringMathematicalPendulum mPendulumSMP = new SpringMathematicalPendulum
            (75., 50., 75., 1., 1., 75., 0.,
                    45. * Math.PI / 180.,
                    5. * Math.PI / 8.,
                    0. * Math.PI / 180.,
                    45. * Math.PI / 180., 9.81 * 100., 0. /1.e6, true, 100, true);
    public static SpringPendulum2D mPendulumSP2D = new SpringPendulum2D
            (75., 50., 1.,
                    75., 0.,
                    75., 0.,
                    9.81 * 100., 0. /1.e6, true, 100, true);
    public static SpringPendulum3D mPendulumSP3D = new SpringPendulum3D
            (75., 50., 1.,
                    75., 0.,
                    75., 0.,
                    75., 0.,
                    9.81 * 100., 0. /1.e6, true, 100, true);
    public static SpringSphericalPendulum mPendulumSSP = new SpringSphericalPendulum
            (75., 75., 1., 1., 50., 0., 0., 75.,
                    45. * Math.PI / 180., 90. * Math.PI / 180.,
                    0., 0., 0.,
                    0. * Math.PI / 180.,
                    0.* Math.PI / 180., 9.81 * 100., 0. /1.e6, true, 100, true);
    public static PendulumWave mPendulumPW = new PendulumWave
            (12, 40, 100., 1., 30. * Math.PI / 180.,
                    9.81 * 100., 0. /1.e6, true, 3000, true);
    private int Width, Height;
    private int mProgram;
    public volatile int mIndex;

    public PendulumRenderer(GenericPendulum pendulum) {
        super();
        mPendulum = pendulum;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.f, 0.f, 0.f, 1.0f);

        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glLineWidth(2.0f);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables

        mPendulum.mProgram = mProgram;
    }

    public void recompileProgram() {
        mPendulum.mProgram = mProgram;
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        mPendulum.preDraw();
        GLES20.glUseProgram(mPendulum.mProgram);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setIdentityM(mPendulum.mVMatrix, 0);
        Matrix.translateM(mPendulum.mVMatrix, 0, 0.0f, -0.2f*Height, -10.0f);


        // Draw pendulum
//        Log.v("Wallpaper renderer", "mIndex: " + mIndex);
//        Log.v("Wallpaper renderer", "Drawing: " + this.mPendulum.name);
        mPendulum.draw(unused, Width, Height);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        Width = width;
        Height = height;
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //perspectiveGL(45.f, ratio, 0.1f, 500.f);
        perspectiveGL(mPendulum.mProjMatrix, 45.f, ratio, 0.1f, 500.f);
        //GLU.gluPerspective(unused, 45.f, ratio, 0.1f, 1200.f);

    }

    public static void perspectiveGL(float[] ProjectionMatrix, float fovY, float aspect, float zNear, float zFar)
    {
        final float pi = (float) 3.1415926535897932384626433832795;
        float fW, fH;
        fH = (float) (Math.tan( fovY / 360 * pi ) * zNear);
        fW = (float) (fH * aspect);
        Matrix.frustumM(ProjectionMatrix, 0, -fW, fW, -fH, fH, zNear, zFar);
        //GLES20.glFrustumf( -fW, fW, -fH, fH, zNear, zFar );
    }

    private final String vertexShaderCode =
            "struct DirectionalLight { \n" +
                    "vec3 direction; \n" +
                    "vec3 halfplane; \n" +
                    "vec4 ambientColor; \n" +
                    "vec4 diffuseColor; \n" +
                    "vec4 specularColor; \n" +
                    "}; \n" +
                    "struct Material { \n" +
                    "    vec4 ambientFactor; \n" +
                    "    vec4 diffuseFactor; \n" +
                    "    vec4 specularFactor; \n" +
                    "    float shininess; \n" +
                    "}; \n" +
                    "// Light \n" +
                    "uniform DirectionalLight u_directionalLight; \n" +
                    "// Material \n" +
                    "uniform Material u_material; \n" +
                    "// Matrices \n" +
                    "uniform mat4 u_mvMatrix; \n" +
                    "uniform mat4 u_mvpMatrix; \n" +
                    "uniform vec4 color \n;" +
                    "// Attributes \n" +
                    "attribute vec4 a_position; \n" +
                    "attribute vec4 a_color; \n" +
                    "attribute vec3 a_normal; \n" +
                    "// Varyings \n" +
                    "varying vec4 v_light; \n" +
                    "varying vec4 v_color; \n" +
                    "void main() { \n" +
                    "    // Define position and normal in model coordinates \n" +
                    "    vec4 mcPosition = a_position; \n" +
                    "    vec3 mcNormal = a_normal; \n" +
                    "    // Calculate and normalize eye space normal \n" +
                    "    vec3 ecNormal = vec3(u_mvMatrix * vec4(mcNormal, 0.0)); \n" +
                    "    ecNormal = ecNormal / length(ecNormal); \n" +
                    "    // Do light calculations \n" +
                    "    float ecNormalDotLightDirection = max(0.0, dot(ecNormal, u_directionalLight.direction)); \n" +
                    "    float ecNormalDotLightHalfplane = max(0.0, dot(ecNormal, u_directionalLight.halfplane)); \n" +
                    "    // Ambient light \n" +
                    "    vec4 ambientLight = u_directionalLight.ambientColor * color;//u_material.ambientFactor; \n" +
                    "    // Diffuse light \n" +
                    "    vec4 diffuseLight = ecNormalDotLightDirection * u_directionalLight.diffuseColor * color;//u_material.diffuseFactor; \n" +
                    "    // Specular light \n" +
                    "    vec4 specularLight = vec4(0.0); \n" +
                    "    if (ecNormalDotLightHalfplane > 0.0) { \n" +
                    "        specularLight = pow(ecNormalDotLightHalfplane, u_material.shininess) * u_directionalLight.specularColor * u_material.specularFactor; \n" +
                    "    } \n" +
                    "    v_light = ambientLight + diffuseLight + specularLight; \n" +
                    "    v_color = a_color; \n" +
                    "    gl_Position = u_mvpMatrix * mcPosition; \n" +
                    "}";


    private final String fragmentShaderCode =
            "precision highp float; \n" +
                    "uniform vec4 color; \n" +
                    "uniform int light; \n" +
                    "uniform int trajectory; \n" +
                    "varying vec4 v_light; \n" +
                    "varying vec4 v_color; \n" +
                    "void main() { \n" +
                    "    if (light>0) gl_FragColor = v_light; \n" +
                    "    else if (trajectory>0) gl_FragColor = v_color; \n" +
                    "    else gl_FragColor = color; \n" +
                    "}";

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void switchPendulum(String pendulum, boolean damping, boolean trace) {
        if (pendulum.equals("0")) this.mPendulum = (GenericPendulum) mPendulumMP;
        else if (pendulum.equals("1")) this.mPendulum = (GenericPendulum) mPendulumSP;
        else if (pendulum.equals("2")) this.mPendulum = (GenericPendulum) mPendulumSP2D;
        else if (pendulum.equals("3")) this.mPendulum = (GenericPendulum) mPendulumSP3D;
        else if (pendulum.equals("4")) this.mPendulum = (GenericPendulum) mPendulumDP;
        else if (pendulum.equals("5")) this.mPendulum = (GenericPendulum) mPendulumDSP;
        else if (pendulum.equals("6")) this.mPendulum = (GenericPendulum) mPendulumSMP;
        else if (pendulum.equals("7")) this.mPendulum = (GenericPendulum) mPendulumSSP;
        else this.mPendulum = (GenericPendulum) mPendulumPW;
        this.mPendulum.setDampingMode(damping);
        this.mPendulum.setTraceMode(trace);
        this.mPendulum.paused = false;
    }
}
