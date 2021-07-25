package edu.android.project.part2_chapter07_recorder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val recordButton: RecordButton by lazy {
        findViewById<RecordButton>(R.id.recordButton)
    }

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    // 사용하지 않을 때는 메모리를 위해 null로 만든다
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var state = State.BEFORE_RECORDING
        set(value) {
            // 새로 들어온 값 넣어주기
            field = value
            // 업데이트
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initViews()
        bindViews()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted = requestCode == REQUSET_RECORD_AUDIO_PERMISSION &&
                // 부여한 결과 하나만 구했으니까
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if(!audioRecordPermissionGranted) {
            // 바로 앱 종료
            finish()
        }
    }

    private fun requestAudioPermission() {
        // 권한 요청
        requestPermissions(requiredPermissions, REQUSET_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun bindViews() {
        recordButton.setOnClickListener {
            when(state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            // internal은 너무 작으니 외부 strage를 사용해야한다
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        state = State.ON_RECORDING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        state = State.AFTER_RECORDING
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepareAsync()
        }
        player?.start()
        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        state = State.AFTER_RECORDING
    }

    companion object {
        private const val REQUSET_RECORD_AUDIO_PERMISSION = 201
    }
}
