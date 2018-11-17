package com.vlvolad.pendulumstudio.springmathematicalpendulum;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.vlvolad.pendulumstudio.common.MiscGL;

public class SMPGLRenderer implements GLSurfaceView.Renderer{
	private static final String TAG = "SMPGLRenderer";
    public static SpringMathematicalPendulum mPendulum = new SpringMathematicalPendulum
    		(75., 50., 75., 1., 1., 75., 0.,
    		45. * Math.PI / 180.,
    		5. * Math.PI / 8., 
    		0. * Math.PI / 180., 
    		45. * Math.PI / 180., 9.81 * 100., 0. /1.e6, true, 100, true);
	public int Width, Height;
    private int mProgram, mProgramAccum;

    private double mPreviousZoomIn;

    public static int[] renderTex    = new int[1];
    public static int[] frameBuffer  = new int[1];

    private void regenerateAccumBuffer() {
        mPreviousZoomIn = mPendulum.zoomIn;

        GLES20.glDeleteBuffers(1, frameBuffer, 0);
        GLES20.glDeleteTextures(1, renderTex, 0);

        GLES20.glGenTextures(1, renderTex, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);

        //Log.d("FBO Create", Integer.toString(Height) + " " + Integer.toString(Width));

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                Width, Height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                null);


        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, renderTex[0], 0);
        //GLES20.glFramebufferRenderbuffer( GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, renderBuffer[0]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Check FBO status.
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

        GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }


    public static void resetAccumBuffer() {
        GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
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


        int vertexShaderAccum   = loadShader(GLES20.GL_VERTEX_SHADER, MiscGL.SHADER_VERTEX_ACCUM);
        int fragmentShaderAccum = loadShader(GLES20.GL_FRAGMENT_SHADER, MiscGL.SHADER_FRAGMENT_ACCUM);
        mProgramAccum = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgramAccum, vertexShaderAccum);   // add the vertex shader to program
        GLES20.glAttachShader(mProgramAccum, fragmentShaderAccum); // add the fragment shader to program
        GLES20.glLinkProgram(mProgramAccum);                  // creates OpenGL ES program executables
    }

    private void appendAccum() {
        GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glUseProgram(mProgram);

        //perspectiveGL(mPendulum.mProjMatrix, 45.0f, (float)(Width)/Height,0.1f,1200.0f);
        SMPGLRenderer.orthoGL(mPendulum.mProjMatrix, Width, Height);

        mPendulum.translateMVMatrix(mPendulum.mVMatrix, Width, Height);

        int tHandle = GLES20.glGetUniformLocation(mProgram, "u_mvpMatrix");
        Matrix.multiplyMM(mPendulum.mMVPMatrix, 0, mPendulum.mProjMatrix, 0, mPendulum.mVMatrix, 0);
        GLES20.glUniformMatrix4fv(tHandle, 1, false, mPendulum.mMVPMatrix, 0);

        mPendulum.mTrajectory.drawNext(mProgram, mPendulum.mMVPMatrix);
        mPendulum.mTrajectory2.drawNext(mProgram, mPendulum.mMVPMatrix);
    }
    
    @Override
    public void onDrawFrame(GL10 unused) {

        mPendulum.preDraw();

        if (SMPSimulationParameters.simParams.showTrajectory && SMPSimulationParameters.simParams.infiniteTrajectory) {
            if (mPreviousZoomIn != mPendulum.zoomIn) {
                resetAccumBuffer();
                mPreviousZoomIn = mPendulum.zoomIn;
            }
            appendAccum();
        }

        GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (SMPSimulationParameters.simParams.showTrajectory && SMPSimulationParameters.simParams.infiniteTrajectory) {
            GLES20.glUseProgram(mProgramAccum);

            GLES20.glDisable(GLES20.GL_DEPTH_TEST);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);
            int tHandle = GLES20.glGetUniformLocation(mProgramAccum, "u_Texture");
            GLES20.glUniform1i(tHandle, 0);

            tHandle = GLES20.glGetAttribLocation(mProgramAccum, "a_Position");
            GLES20.glEnableVertexAttribArray(tHandle);
            GLES20.glVertexAttribPointer(tHandle, 2,
                    GLES20.GL_FLOAT, false,
                    0, MiscGL.vertexBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
            GLES20.glDisableVertexAttribArray(tHandle);

            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        }

        GLES20.glUseProgram(mProgram);
        // Draw pendulum
        mPendulum.draw(unused, Width, Height);

        mPendulum.frames++;
    }
    
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
    	Width = width;
    	Height = height;
    	GLES20.glViewport(0, 0, width, height);

        regenerateAccumBuffer();
    }
    
    public static void orthoGL(float[] ProjectionMatrix, float Width, float Height)
    {
        Matrix.orthoM(ProjectionMatrix, 0, 0, Width, 0, Height, -1000.f, 1000.f);
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
}
