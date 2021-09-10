package edu.android.project.shader_texture

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GL2JNIView(context: Context)
    : GLSurfaceView(context) {

    init {
        setEGLContextClientVersion(3)

        setRenderer(Renderer())
        //renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }


    private class Renderer: GLSurfaceView.Renderer {

        override fun onDrawFrame(gl: GL10?) {
            GL2JNILib.draw()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GL2JNILib.resize(width, height)
        }

        override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
            GL2JNILib.init()
        }
    }
}