package edu.android.project.part2_chapter03_diary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class DiaryActivity: AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val diaryEditText: EditText by lazy {
        findViewById<EditText>(R.id.diaryEditText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)
        // detail 저장값 가져오기
        diaryEditText.setText(detailPreferences.getString("detail", ""))

        val runnable = Runnable {
            detailPreferences.edit(true) {
                putString("detail", diaryEditText.text.toString())
            }

            Log.d("DiaryActivity", "SAVE!!${diaryEditText.text.toString()}")
        }

        diaryEditText.addTextChangedListener(object: TextWatcher{
            // 입력이 끝났을 때
            override fun afterTextChanged(s: Editable?) {
                // 500ms 이전에 아직 runnable이 있다면 지우고 없으면 그냥 두기
                // 그러므로 마지막에 체인지가 없을 때만 실행된다
                Log.d("DiaryActivity", "TextChanged :: $s")
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 500)
            }
            // 입력하기 전 호출
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            // EditText에 변화가 있을 때
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

}