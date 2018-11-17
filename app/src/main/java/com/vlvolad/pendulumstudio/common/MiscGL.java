package com.vlvolad.pendulumstudio.common;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Volodymyr on 18.06.2017.
 */

public class MiscGL {
    public static final FloatBuffer vertexBuffer;  //must be initialized here.

    static {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                6 * 2 * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();

        vertexBuffer.put(-1.f);
        vertexBuffer.put(-1.f);


        vertexBuffer.put(-1.f);
        vertexBuffer.put(1.f);

        vertexBuffer.put(1.f);
        vertexBuffer.put(-1.f);

        vertexBuffer.put(1.f);
        vertexBuffer.put(-1.f);

        vertexBuffer.put(-1.f);
        vertexBuffer.put(1.f);

        vertexBuffer.put(1.f);
        vertexBuffer.put(1.f);

        vertexBuffer.position(0);
    }

    public static void regenerateAccumBuffer(int[] frameBuffer, int[] renderTexture, int Width, int Height) {
        GLES20.glDeleteBuffers(1, frameBuffer, 0);
        GLES20.glDeleteTextures(1, renderTexture, 0);

        GLES20.glGenTextures(1, renderTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTexture[0]);

        Log.d("FBO Create", Integer.toString(Height) + " " + Integer.toString(Width));

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
                GLES20.GL_TEXTURE_2D, renderTexture[0], 0);
        //GLES20.glFramebufferRenderbuffer( GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, renderBuffer[0]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Check FBO status.
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

        if ( status == GLES20.GL_FRAMEBUFFER_COMPLETE )
        {
            Log.d("FBO Create", "Framebuffer complete");
        }
        else {
            Log.d("FBO Create", "Framebuffer failed: " + Integer.toString(status));
        }

        GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    public static void resetBuffer(int[] frameBuffer) {
        GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    public static final String SHADER_VERTEX_ACCUM = ""
            + "attribute vec4 a_Position;\n"
            + "varying highp vec2 v_TexCoordinate;\n"
            + "void main() {\n"
            + "  v_TexCoordinate = a_Position.xy * 0.5 + 0.5;\n"
            + "  gl_Position = a_Position;\n"
            + "}\n";

    public static final String SHADER_FRAGMENT_ACCUM = ""
            + ""
            + "uniform sampler2D u_Texture;\n"
            + "varying highp vec2 v_TexCoordinate;\n"
            + "void main() {\n"
            + "  gl_FragColor = texture2D(u_Texture, v_TexCoordinate);\n"
            + "}\n";
}
