package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.earlybird.runningbuddy.databinding.ActivitySplashBinding
import com.earlybird.runningbuddy.databinding.ActivityUserInfoBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// 유저 정보
class UserInfo : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ab: ActionBar? = supportActionBar
        ab?.setTitle("내 정보")
        val db: FirebaseFirestore = Firebase.firestore
        val currentUid = Firebase.auth.currentUser?.uid ?: "No User"

        db.collection("users").document(currentUid).get().addOnSuccessListener {
            binding.userNameView.text = it["Name"].toString()
            binding.userHeightView.text = it["Height"].toString()
            binding.userWeightView.text = it["Weight"].toString()
            binding.userSexView.text = it["Sex"].toString()
            binding.calculateBMI.text =
                calculateBMI(it["Height"].toString().toDouble(), it["Weight"].toString().toDouble())
        }.addOnFailureListener {
            Toast.makeText(this, "내 정보 불러오기 실패. \n 다시 Login 해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    fun calculateBMI(Height: Double, Weight: Double): String {
        val calResult = Weight / ((Height * Height) / 10000)
        if (calResult > 25) {
            return "비만"
        }
        else if (calResult > 23) {
            return "과제중"
        }
        else if (calResult > 18.5) {
            return "정상"
        }
        else if (calResult <= 18.5){
            return "저체중"
        } else {
            return "정확한 체중과 신장을 입력해주세요"
        }
    }
}