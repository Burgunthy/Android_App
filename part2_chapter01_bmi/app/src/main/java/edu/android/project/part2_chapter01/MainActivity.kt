package edu.android.project.part2_chapter01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val heightEditText: EditText = findViewById(R.id.heightEditText)
        val weightEditText = findViewById<EditText>(R.id.weightEditExt)

        val resultButton = findViewById<Button>(R.id.resultButton)

        resultButton.setOnClickListener {
            Log.d("MainActivity", "ResultButton 이 클릭되었습니다.")

            // 빈 값이 아니라면 에러가 나서 죽는다 -> 로그를 보면서 분석하기
            if(heightEditText.text.isEmpty() || weightEditText.text.isEmpty()) {
                // 토스트 메세지
                Toast.makeText(this, "빈 값이 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 이 아래는 빈 값이 아니다

            val height: Int = heightEditText.text.toString().toInt()
            val weight: Int = weightEditText.text.toString().toInt()

            Log.d("MainActivity", "height : $height weight : $weight")

            // 전 액티비티 // 다음 액티비티
            val intent = Intent(this, ResultActivity::class.java)

            intent.putExtra("height", height)
            intent.putExtra("weight", weight)

            startActivity(intent)
        }

    }
}
