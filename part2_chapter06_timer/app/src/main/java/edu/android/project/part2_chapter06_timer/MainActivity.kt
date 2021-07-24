package edu.android.project.part2_chapter06_timer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById<TextView>(R.id.remainMinutesTextView)
    }
    private val remainSecondsTextView: TextView by lazy {
        findViewById<TextView>(R.id.remainSecondsTextView)
    }
    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.seekBar)
    }

    private var currentCountDownTimer : CountDownTimer? = null

    private val soundPool = SoundPool.Builder().build()

    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 간단한 이름으로 표현하기!!!
        bindVeiws()
        initSounds()
    }

    override fun onResume() {
        super.onResume()

        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()

        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        soundPool.release()
    }

    private fun bindVeiws() {
        // view에 대한 전반적인 설정을 다룬다
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // 값이 변경되었을 때
                    if(fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // seekBar를 바꿀 때. 그러니까 멈춰야지
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // 손을 때는 순간 작동하는 task
                    seekBar ?: return

                    if(seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
                }

            }
        )
    }

    private fun initSounds() {
        // 사용할 sound를 지정한다
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun startCountDown() {
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundId?.let { soundId->
            soundPool.play(soundId, 1F, 1F ,0, -1, 1f)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null

        soundPool.autoPause()
    }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekBar(0)

        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

    private fun createCountDownTimer(initialMillis: Long): CountDownTimer =
            object : CountDownTimer(initialMillis, 1000) {
                override fun onFinish() {
                    // 카운트 다운이 종료되었을 때
                    completeCountDown()
                }

                override fun onTick(millisUntilFinished: Long) {
                    // 카운트 다운의 각 tick마다
                    updateRemainTime(millisUntilFinished)
                    updateSeekBar(millisUntilFinished)
                }

            }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long) {
        // 초단위로 바꿔준다
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }
    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

}
