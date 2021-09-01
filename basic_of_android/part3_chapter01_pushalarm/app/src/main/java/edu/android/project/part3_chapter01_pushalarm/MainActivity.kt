package edu.android.project.part3_chapter01_pushalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }
    private val firebaseTokenTextView: TextView by lazy {
        findViewById<TextView>(R.id.firebaseTokenTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFireBase()
    }

    private fun initFireBase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    firebaseTokenTextView.text = task.result
                }
            }
    }
}
