package edu.android.project.part2_chapter03_diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    // numberPicker에 미리 min, max value 설정
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }
    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    // 자동으로 bool 형식으로 선언
    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 미리 apply를 위해 단순히 선언
        numberPicker1
        numberPicker2
        numberPicker3

        // openButton 클릭 시
        // 패스워드에 따라 다이어리를 열건지 결정
        openButton.setOnClickListener {

            if(changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            // 위 두 변수의 값이 같을 때 diary를 open한다
            if(passwordPreferences.getString("keyword", "000").equals(passwordFromUser)) {
                // 새로운 Activity 작성하여 Diary 화면 띄우기
                // 패스워드 성공 시
                startActivity(Intent(this, DiaryActivity::class.java))
            }
            else {
                // 비밀번호가 틀렸다고 말하기
                showErrorAlertDialog("비밀번호가 틀렸습니다")
            }
        }

        // chagnePasswordButton 클릭 시
        changePasswordButton.setOnClickListener {

            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if(changePasswordMode) {
                // 변경중일 때 password 변경
                passwordPreferences.edit(true) {
                    putString("keyword", passwordFromUser)
                }

                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)
            }
            else {
                // 변경할 때
                if(passwordPreferences.getString("keyword", "000").equals(passwordFromUser)) {
                    // 비밀번호가 맞을 때
                    changePasswordMode = true
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                    changePasswordButton.setBackgroundColor(Color.RED)
                }
                else {
                    // 비밀번호가 틀렸다고 말하기
                    showErrorAlertDialog("비밀번호가 틀렸습니다. 변경이 불가합니다")
                }
            }
        }
    }

    private fun showErrorAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("실패")                               // 팝업 창의 이름 의미
            .setMessage(message)        // 팝업 창 내 메세지
            .setPositiveButton("확인") { _, _ -> }   // setOnClickListener. 원하는 곳으로 이동
            .create()                                       // 생성
            .show()                                         // 출력
    }
}
