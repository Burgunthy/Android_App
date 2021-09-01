package edu.android.project.gles_texture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var mGLView: GLSurfaceView
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setImage(0)
        setImage(1)

        mGLView = GL2JNIView(this)
        setContentView(mGLView)
    }

    private fun setImage(idx: Int) {
        val inputStream: InputStream
        val name: String = "img_" + String.format("%04d", idx) + ".png"
        val inputImage = Mat()

        inputStream = assets.open(name)                 // name에 해당하는 파일 open

        bitmap = BitmapFactory.decodeStream(inputStream)
        Utils.bitmapToMat(bitmap, inputImage)

        GL2JNILib.setImageAddr(inputImage.nativeObjAddr)
    }

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
