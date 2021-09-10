package edu.android.project.shader_texture

object GL2JNILib {

    external fun changePose(idx: Int)

    external fun setImageAddr(ImageAddr: Long)
    external fun setDepthAddr(ImageAddr: Long)

    external fun init()
    external fun resize(w: Int, h: Int)
    external fun draw()

}