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

        val detailPreference = getSharedPreferences("diary", Context.MODE_PRIVATE)

        // detail 값 가져오기
        diaryEditText.setText(detailPreference.getString("detail", ""))



        val runnable = Runnable {
            // background에서 계속 저장 -> apply를 이용해서 (비동기)
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("detail", diaryEditText.text.toString())
            }

            Log.d("DiaryActivity", "SAVE!!!! ${diaryEditText.text.toString()}")
        }

        // 유실되지 않게 하기
        diaryEditText.addTextChangedListener(object : TextWatcher {
            // 입력이 끝났을 때
            override fun afterTextChanged(p0: Editable?) {
                // 500ms 이전에 아직 runnable이 있다면 지우고 없으면 그냥 두기
                // 그러므로 마지막에 체인지가 없을 때만 실행된다
                Log.d("DiaryActivity", "TextChanged :: $p0")
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 500)
            }
            // 입력하기 전 호출
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            // EditText에 변화가 있을 때
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


    }
}