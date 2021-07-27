package edu.android.project.part2_chapter07_recorder

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpTextView(
    context: Context,
    attrs: AttributeSet
): AppCompatTextView(context, attrs) {

    // 시작 시간과 진행되는 시간을 저장하기
    private var startTimeStamp: Long = 0L

    private val countUpAction: Runnable =
        object : Runnable {
            override fun run() {
                val currentTimeStamp = SystemClock.elapsedRealtime()
                val countTimeSeconds = ((currentTimeStamp - startTimeStamp)/1000L).toInt()
                updateCountTime(countTimeSeconds)

                handler?.postDelayed(this, 1000L)
            }
        }

    // 글씨 바꿔지는 과정 넣기
    fun startCountUp() {
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp() {
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountTime() {
        updateCountTime(0)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountTime(countTimeSeconds: Int) {
        val minutes = countTimeSeconds / 60
        val seconds = countTimeSeconds % 60

        text = "%02d:%02d".format(minutes, seconds)
    }
}