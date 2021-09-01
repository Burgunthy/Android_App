package edu.android.project.gles_3d

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR
import org.opencv.imgcodecs.Imgcodecs.imread
import java.io.*


class MainActivity : AppCompatActivity() {

    private lateinit var mGLView: GLSurfaceView
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setImage2(0)

        mGLView = GL2JNIView(this)

        addContentView(
            mGLView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun onPause() {
        super.onPause()
        mGLView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLView.onResume()
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

    private fun setImage2(idx: Int) {
        val inputStream: InputStream
        val name: String = "img_" + String.format("%04d", idx) + ".png"

        copyFile(name)

        val path = "$filesDir/$name"
        val inputImage = imread(path, IMREAD_COLOR)

        // 삭제
        val directory = File("$filesDir/$name")

        if (directory.exists()) {
            directory.delete()
        }

        Log.d(TAG, "w : ${inputImage.cols()}, h : ${inputImage.rows()}, ch : ${inputImage.channels()}, type : ${inputImage.type()}")

        GL2JNILib.setImageAddr(inputImage.nativeObjAddr)
    }

    private fun copyFile(f: String) {
        val `in`: InputStream
        try {
            `in` = assets.open(f)
            //val of = File(getDir("execdir", Context.MODE_PRIVATE), f)
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
        Toast.makeText(this, "Left가 눌렸습니다.", Toast.LENGTH_SHORT).show()
    }

    fun onClickRight(view: View) {
        Toast.makeText(this, "Right가 눌렸습니다.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = MainActivity::class.simpleName
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
