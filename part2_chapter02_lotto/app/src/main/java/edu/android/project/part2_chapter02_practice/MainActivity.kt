package edu.android.project.part2_chapter02_practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val clearButton: Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    private val runButton: Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView> (
            findViewById<TextView>(R.id.firstTextView),
            findViewById<TextView>(R.id.secondTextView),
            findViewById<TextView>(R.id.thirdTextView),
            findViewById<TextView>(R.id.forthTextView),
            findViewById<TextView>(R.id.fifthTextView),
            findViewById<TextView>(R.id.sixthTextView)
        )
    }

    private var didRun = false
    private var pickNumberSet = hashSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()
    }

    private fun initRunButton() {
        runButton.setOnClickListener {

            val list = getRandomNumber()

            didRun = true

            // list를 index와 값으로 불러온다!
            list.forEachIndexed {index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true

                setNumberBackground(number, textView)
            }
        }
    }

    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>()
            .apply {
                // 45개중 이미 있는 것을 제외하고 어레이에 추가한다
                for(i in 1..45) {
                    if (pickNumberSet.contains(i)) {
                        continue
                    }
                    this.add(i)
                }
            }

        numberList.shuffle()

        // 자동으로 타입이 정해지는구나
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)
        return newList.sorted()
    }

    private fun initAddButton() {
        addButton.setOnClickListener {

            if(didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5까지만 선택 가능", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 값 list에 추가하기
            pickNumberSet.add(numberPicker.value)

            // activity 변경
            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()

            // 배경색 변경 -> 여기 함수화 가능
            setNumberBackground(pickNumberSet.last(), textView)

        }
    }

    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()               // 모두 초기화
            numberTextViewList.forEach {
                it.isVisible = false
            }
        }
    }

    private fun setNumberBackground(number:Int, textView: TextView) {
        when(numberPicker.value) {
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

}
