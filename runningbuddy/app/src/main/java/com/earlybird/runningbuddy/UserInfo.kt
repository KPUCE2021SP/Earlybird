package com.earlybird.runningbuddy

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.earlybird.runningbuddy.databinding.ActivitySplashBinding
import com.earlybird.runningbuddy.databinding.ActivityUserInfoBinding
import com.earlybird.runningbuddy.databinding.AlertdialogEdittextBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
        binding.weightLayout.setOnClickListener() {
            val builderItem = AlertdialogEdittextBinding.inflate(layoutInflater)
            val editText = builderItem.editText

            AlertDialog.Builder(this).apply {
                setTitle("체중 변경")
                setMessage("내 정보 화면 다시 입장시 반영됩니다.")
                setView(builderItem.root)
                setPositiveButton("OK") { _: DialogInterface, _: Int ->
                    if (editText.text.toString() != "") {
                        val infoMap = hashMapOf(
                            "Weight" to editText.text.toString().toDouble()
                        )
                        db.collection("users")
                            .document(Firebase.auth.currentUser?.uid ?: "No User")
                            .set(infoMap, SetOptions.merge())
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "제대로된 값을 입력 해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.show()
            }
        }
        binding.heightLayout.setOnClickListener() {
            val builderItem = AlertdialogEdittextBinding.inflate(layoutInflater)
            val editText = builderItem.editText

            AlertDialog.Builder(this).apply {
                setTitle("신장 변경")
                setMessage("내 정보 화면 다시 입장시 반영됩니다.")
                setView(builderItem.root)
                setPositiveButton("OK") { _: DialogInterface, _: Int ->
                    if (editText.text.toString() != "") {
                        val infoMap = hashMapOf(
                            "Height" to editText.text.toString().toDouble()
                        )
                        db.collection("users")
                            .document(Firebase.auth.currentUser?.uid ?: "No User")
                            .set(infoMap, SetOptions.merge())
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "제대로된 값을 입력 해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.show()
            }
        }
        binding.nameLayout.setOnClickListener() {
            val builderItem = AlertdialogEdittextBinding.inflate(layoutInflater)
            val editText = builderItem.editText

            AlertDialog.Builder(this).apply {
                setTitle("이름 변경")
                setMessage("내 정보 화면 다시 입장시 반영됩니다.")
                setView(builderItem.root)
                setPositiveButton("OK") { _: DialogInterface, _: Int ->
                    if (editText.text.toString() != "") {
                        val infoMap = hashMapOf(
                            "Name" to editText.text.toString()
                        )
                        db.collection("users")
                            .document(Firebase.auth.currentUser?.uid ?: "No User")
                            .set(infoMap, SetOptions.merge())
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "제대로된 값을 입력 해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.show()
            }
        }
    }


    fun calculateBMI(Height: Double, Weight: Double): String {
        val calResult = Weight / ((Height * Height) / 10000)
        if (calResult > 25) {
            return "비만"
        } else if (calResult > 23) {
            return "과체중"
        } else if (calResult > 18.5) {
            return "정상"
        } else if (calResult <= 18.5) {
            return "저체중"
        } else {
            return "정확한 체중과 신장을 입력해주세요"
        }
    }
}