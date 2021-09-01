package edu.android.project.part2_chapter06_timer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
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

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindVeiws()
        initSounds()
    }

    override fun onResume() {
        super.onResume()

        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()

        // 모든 소리를 다 제거
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindVeiws() {
        // seekBar 설정 방법 알기
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                // @SuppressLint("SetTextI18n")
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // remainMinutesTextView.text = "%02d".format(progress)
                    // 사용자가 건들였을 때만 하도록 실행
                    if(fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                // 조작 시 멈추기 위하여
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // 현재 카운트다운을 할당
                    stopCountDown()
                }

                // 손을 때는 순간 작동
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // ? 연산자인가
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
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

//    private fun createCountDownTimer(initialMillis: Long): CountDownTimer {
//        return object: CountDownTimer(initialMillis, 1000) {
//            override fun onFinish() {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onTick(p0: Long) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//        }
//    }

    private fun createCountDownTimer(initialMillis: Long): CountDownTimer =
        object: CountDownTimer(initialMillis, 1000) {
            override fun onFinish() {
                completeCountDown()
            }

            // 1000 마다 틱이 한번씩 불린다
            override fun onTick(millisUntilFinished: Long) {
                // text와 seekBar 갱신
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }
        }

    private fun startCountDown() {
        // 생성과 동시에 시작한다
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        //  변화가 있으면 달라질 수 있으니 null로 선언
        currentCountDownTimer?.start()

        // null이 아닐 때 해당 함수 호출!!
        tickingSoundId?.let {soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1f)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        // 음악도 멈춰야 하니까
        soundPool.autoPause()
    }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let {soundId ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

}
