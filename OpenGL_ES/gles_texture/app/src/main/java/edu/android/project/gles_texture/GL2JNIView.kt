package edu.android.project.gles_texture

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GL2JNIView(context: Context)
    : GLSurfaceView(context) {

    init {

        setEGLContextClientVersion(2)

        setRenderer(Renderer())
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }


    private class Renderer: GLSurfaceView.Renderer {

        private lateinit var mSquare: Square

        private var startTime: Long = 0
        private var endTime: Long = 0

        override fun onDrawFrame(gl: GL10?) {

            startTime = System.currentTimeMillis()
            GL2JNILib.draw()
            endTime = System.currentTimeMillis()
            Log.d("GL2JNIView", "GL2JNILib.draw() : ${endTime - startTime}")

            startTime = System.currentTimeMillis()
            //mSquare.draw()
            endTime = System.currentTimeMillis()
            Log.d("GL2JNIView", "mSquare.draw() : ${endTime - startTime}")

        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            GL2JNILib.resize(width, height)
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // Set the background frame color

            startTime = System.currentTimeMillis()
            GL2JNILib.init()
            endTime = System.currentTimeMillis()
            Log.d("GL2JNIView", "GL2JNILib.init() : ${endTime - startTime}")

            startTime = System.currentTimeMillis()
            //mSquare = Square()
            endTime = System.currentTimeMillis()
            Log.d("GL2JNIView", "mSquare = Square() : ${endTime - startTime}")
        }

    }
}