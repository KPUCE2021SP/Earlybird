package com.earlybird.runningbuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityDataviewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DataViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataviewBinding
    private lateinit var recordListIntent: Intent  //Intent for recordListActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recordListIntent = Intent(applicationContext, RecordListActivity::class.java)

        binding.recordButton.setOnClickListener {
            startActivity(recordListIntent)
        }

        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        viewRecord()

    }

    override fun onBackPressed() {
        return
    }

    @SuppressLint("SetTextI18n")
    private fun viewRecord() {

        val time = intent.getDoubleExtra("Time", 0.0)
        val distance = intent.getDoubleExtra("Distance", 0.0)
        var weight: String

        binding.runTimeTextView.text = intent.getStringExtra("FormatTime")
        binding.runDistanceTextView.text = "%.1f km".format(distance)

        // 칼로리 계산을 위해 사용자의 체중을 가져오기
        val db: FirebaseFirestore = Firebase.firestore

        db.collection("users")
            .document(Firebase.auth.currentUser?.uid ?: "No User").get()
            .addOnSuccessListener {
                weight = it["Weight"].toString()
                binding.runCalorieTextView.text = getCalorie(weight.toDouble(), time)
            }
    }

    // 1분기준으로, 걷기 운동계수는 0.07 정도, 운동 강도에 따라 달라질 수 잇음을 사용자에게 인지시키기 필요
    private fun getCalorie(weight: Double, time: Double): String {
        val calorie = (weight * (time / 60) * 0.07)
        return String.format("%.1f", calorie)
    }
}