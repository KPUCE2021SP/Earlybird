package com.earlybird.runningbuddy.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
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
        val ab : ActionBar? = supportActionBar
        ab?.setTitle("러닝결과")
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

    private fun viewRecord() {

        val time = intent.getDoubleExtra("Time", 0.0)
        val distance = intent.getDoubleExtra("Distance", 0.0)
        var weight: String = "null"

        binding.runTimeTextView.text = intent.getStringExtra("FormatTime")
        binding.runDistanceTextView.text = "%.1f km".format(distance)
        binding.runPaceTextView.text = getAveragePace(distance, time)
        binding.runSpeedTextView.text = getAverageSpeed(distance, time)

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


    //    평균 속도 구하는 식
    private fun getAverageSpeed(distance: Double, time: Double): String {
        val averageSpeed = ( distance / (time * 3600))  //시간당 거리를 구한다.
        return String.format("%.1f km/h", averageSpeed)
    }

    // 평균 페이스 구하는 식 (1km당 걸린 시간 = 페이스)
    private fun getAveragePace(distance: Double, time: Double): String {
        val averagePcae = (time / distance)
        return String.format("km당 %.1f 초", averagePcae) //페이스는 1km당 걸린 시간을 초단위로 출력
    }
}