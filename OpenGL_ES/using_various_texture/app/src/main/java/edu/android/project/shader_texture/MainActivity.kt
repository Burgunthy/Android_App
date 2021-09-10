package edu.android.project.shader_texture

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.imgcodecs.Imgcodecs
import java.io.*

class MainActivity : AppCompatActivity() {

    private lateinit var mGLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setImage(0)
        setDepth(0)

        mGLView = GL2JNIView(this)

        addContentView(
            mGLView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun setImage(idx: Int) {
        val name: String = "img_" + String.format("%04d", idx) + ".png"

        copyFile(name)

        val path = "$filesDir/$name"
        val inputImage = Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR)

        // 삭제
        val directory = File("$filesDir/$name")
        if (directory.exists()) {
            directory.delete()
        }

        Log.d(TAG, "w : ${inputImage.cols()}, h : ${inputImage.rows()}, ch : ${inputImage.channels()}, type : ${inputImage.type()}")

        GL2JNILib.setImageAddr(inputImage.nativeObjAddr)
    }

    private fun setDepth(idx: Int) {
        val name: String = "depth_" + String.format("%04d", idx) + ".png"

        copyFile(name)

        val path = "$filesDir/$name"
        val inputDepth = Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR)

        // 삭제
        val directory = File("$filesDir/$name")
        if (directory.exists()) {
            directory.delete()
        }

        Log.d(TAG, "w : ${inputDepth.cols()}, h : ${inputDepth.rows()}, ch : ${inputDepth.channels()}, type : ${inputDepth.type()}")

        GL2JNILib.setDepthAddr(inputDepth.nativeObjAddr)
    }

    private fun copyFile(f: String) {
        val `in`: InputStream
        try {
            `in` = assets.open(f)
            val of = File(filesDir, f)

            Log.d(TAG, "where: $filesDir")
            val out: OutputStream = FileOutputStream(of)
            val b = ByteArray(65535)
            var sz = 0
            while (`in`.read(b).also { sz = it } > 0) {
                out.write(b, 0, sz)
            }
            `in`.close()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun onClickLeft(view: View) {
        //Toast.makeText(this, "Left가 눌렸습니다.", Toast.LENGTH_SHORT).show()

        GL2JNILib.changePose(1)
    }

    fun onClickUpMiddle(view: View) {
        //Toast.makeText(this, "Middle이 눌렸습니다.", Toast.LENGTH_SHORT).show()

        GL2JNILib.changePose(2)
    }

    fun onClickDownMiddle(view: View) {
        //Toast.makeText(this, "Middle이 눌렸습니다.", Toast.LENGTH_SHORT).show()

        GL2JNILib.changePose(3)
    }

    fun onClickRight(view: View) {
        //Toast.makeText(this, "Right가 눌렸습니다.", Toast.LENGTH_SHORT).show()

        GL2JNILib.changePose(0)
    }


    companion object {
        private val TAG = "MainActivity"
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
