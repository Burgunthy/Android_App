package edu.android.project.gles_3d

object GL2JNILib {

    external fun setImageAddr(ImageAddr: Long)

    external fun init()
    external fun resize(w: Int, h: Int)
    external fun draw()

}