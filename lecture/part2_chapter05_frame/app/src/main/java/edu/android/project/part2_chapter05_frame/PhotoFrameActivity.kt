package edu.android.project.part2_chapter05_frame

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity: AppCompatActivity() {

    private val photoList = mutableListOf<Uri>()

    private var currentPosition = 0

    private var timer: Timer? = null

    private val photoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.photoImageView)
    }
    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.backgroundPhotoImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        Log.d("PhotoFrame", "onCreate!!")

        getPhotoUriFromIntent()
    }

    private fun getPhotoUriFromIntent() {
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            // let을 이용해 null이 아닐 때만 실행하기
            intent.getStringExtra("photo$i")?.let {
                // Uri 형태로 다시 변환해서!
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer() {
        // 5초에 한번씩 무언가 설정 -> onStop에서는 멈추게 하기
        timer = timer(period = 5 * 1000) {
            // main thread에서 실행
            runOnUiThread {

                Log.d("PhotoFrame", "5초 지나감!!")

                val current = currentPosition
                val next = if(photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                backgroundPhotoImageView.setImageURI(photoList[current])

                photoImageView.alpha = 0f
                photoImageView.setImageURI(photoList[next])
                // 0에서 1까지 주면서 이미지 불러오기
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }

    override fun onStop() {
        super.onStop()

        Log.d("PhotoFrame", "onStop!! timer cancel")

        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()

        Log.d("PhotoFrame", "onStart!! timer start")

        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("PhotoFrame", "onDestroy!! timer cancel")

        timer?.cancel()
    }

}