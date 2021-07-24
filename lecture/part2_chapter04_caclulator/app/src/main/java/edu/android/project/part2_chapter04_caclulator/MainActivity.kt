package edu.android.project.part2_chapter04_caclulator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import edu.android.project.part2_chapter04_caclulator.model.History
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }

    private val historyLayout: View by lazy {
        findViewById<View>(R.id.historyLayout)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }

    lateinit var db: AppDatabase

    private var isOperator = false
    private var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    fun buttonClicked(v: View) {
        when(v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")

            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")
        }
    }

    private fun numberButtonClicked(number: String) {
        // 연산자 넣기
        if (isOperator) {
            expressionTextView.append(" ")
        }
        isOperator = false

        // 띄어쓰기로 계산한다
        val expressionText = expressionTextView.text.split(" ")

        // 연산자 15자 이상일 때
        if(expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리 까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        expressionTextView.append(number)
        // TODO resultTextView 실시간으로 계산 결과 넣기
        resultTextView.text = calculateExpression()
    }

    @SuppressLint("SetTextI18n")
    private fun operatorButtonClicked(operator: String) {
        // 아무것도 없다면 에러 출력
        if(expressionTextView.text.isEmpty()) {
            return
        }
        //
        when {
            isOperator -> {
                // 오퍼레이터 변경하기
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator
            }
            hasOperator -> {
                // 이미 사용한 경우라면
                Toast.makeText(this, "연산자는 한 번만 사용할 수 있습니다", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressionTextView.append(" $operator")
            }
        }

        // 계산기 구현 시 색을 다르게 구현 -> 색을 다르게 하기
        // 연산자만 green으로 변경
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,     // 시작
            expressionTextView.text.length,                // 마무리
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        expressionTextView.text = ssb

        // 변수 왜 설정했을지 파악하기
        isOperator = true
        hasOperator = true
    }

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")

        // 아무것도 없을 때
        if(expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        // 수식이 3개가 아닐 때
        if(expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니댜.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        // TODO 디비에 넣어주는 부분
        // main과 이거중 뭐가 시작될지 몰라
        Thread(Runnable {
            // Primary key라 하나씩 자동 증가
            db.historyDAO().insertHistory(History(null, expressionText, resultText))
        }).start()

        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false
    }

    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")

        // 숫자 연산자 숫자
        if(hasOperator.not() || expressionTexts.size != 3) {
            return ""
        }
        else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }

    // 함수 확장하기
    fun String.isNumber(): Boolean {
        return try {
            // Declaration을 참고해서 보기
            this.toBigInteger()
            true
        } catch(e: NumberFormatException) {
            false
        }
    }

    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true
        historyLinearLayout.removeAllViews()

        // TODO 디비에서 모든 기록 가져오기
        Thread(Runnable {
            db.historyDAO().getAll().reversed().forEach() {
                // Main Thread에서 실행
                runOnUiThread {
                    // 아래 공부해보기
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "= ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()


        // TODO 뷰에 모든 기록 할당하기
    }

    fun closeHistoryButtonCliked(v: View) {
        historyLayout.isVisible = false
    }

    fun historyClearButtonClicked(v: View) {

        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDAO().deleteAll()
        }).start()

        // TODO 디비에서 모든 기록 삭제
        // TODO 뷰에서 모든 기록 삭제
    }
}
