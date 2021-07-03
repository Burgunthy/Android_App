package edu.android.project.part2_chapter02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private val clearButton : Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    private val addButton : Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val runButton : Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView> (
            findViewById<TextView>(R.id.textView1),
            findViewById<TextView>(R.id.textView2),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView6)
        )
    }

    private var didRun = false          //error state
    private val pickNumberSet = hashSetOf<Int>()


    // number picker

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
           val list = getRandomNumber()         // 선택 번호 제외?

           didRun = true

           list.forEachIndexed { index, number ->
               val textView = numberTextViewList[index]

               textView.text = number.toString()
               textView.isVisible = true


               //when(numberPicker.value) {
               //    in 1.. 10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
               //    in 11.. 20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
               //    in 21.. 30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
               //    in 31.. 40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
               //    else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
               //}

               setNumberBackground(number, textView)
           }

           // Log.d("MainActivity", list.toString())
       }
    }

    private fun initAddButton() {
        addButton.setOnClickListener {
            if(didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택 가능. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()

            //when(numberPicker.value) {
            //    in 1.. 10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            //    in 11.. 20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            //    in 21.. 30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            //    in 31.. 40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            //    else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
            //}

            setNumberBackground(numberPicker.value, textView)

            pickNumberSet.add(numberPicker.value)
        }
    }

    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach {        // 순차적으로 하나씩 꺼냄
                it.isVisible = false
            }
        }
    }

    private fun setNumberBackground(number:Int, textView: TextView) {
        when(numberPicker.value) {
            in 1.. 10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11.. 20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21.. 30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31.. 40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    private fun getRandomNumber(): List<Int> {

        val numberList = mutableListOf<Int>()
            .apply {
                for(i in 1..45) {
                    if(pickNumberSet.contains(i)) {
                        continue
                    }

                    this.add(i)
                }
            }

        numberList.shuffle()

        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)

        return newList.sorted()
    }
}
