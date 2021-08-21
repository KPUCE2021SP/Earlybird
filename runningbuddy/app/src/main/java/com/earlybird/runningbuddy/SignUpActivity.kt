package com.earlybird.runningbuddy

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.earlybird.runningbuddy.databinding.ActivityAfterLoginBinding
import com.earlybird.runningbuddy.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db: FirebaseFirestore = Firebase.firestore

        var sex : String = ""
        binding.MaleButton.setOnClickListener {
            binding.sexView.text = "남자"
            sex = "male"
        }

        binding.FemaleButton.setOnClickListener {
            binding.sexView.text = "여자"
            sex = "female"
        }

        binding.EndButton.setOnClickListener {

            if (binding.NameText.text.toString().equals("")
                || binding.HeightText.text.toString().equals("")
                || binding.WeightText.text.toString().equals("")
                || sex.equals("")
            ) {

                Toast.makeText(this, "빈칸을 모두 채워주세요", Toast.LENGTH_SHORT).show()

            } else {

                Firebase.auth.createUserWithEmailAndPassword(
                    binding.IDtext.text.toString(),
                    binding.PWtext.text.toString()

                ).addOnCompleteListener(this) { // it: Task<AuthResult!>

                    if (it.isSuccessful) {
                        val infoMap = hashMapOf(
                            "Name" to binding.NameText.text.toString(),
                            "Height" to binding.HeightText.text.toString().toDouble(),
                            "Weight" to binding.WeightText.text.toString().toDouble(),
                            "sex" to sex
                        )

                        db.collection("users")
                            .document(Firebase.auth.currentUser?.uid ?: "No User")
                            .set(infoMap)

                        startActivity(
                            Intent(this, LoginActivity::class.java)
                        )

                    } else {
                        Toast.makeText(this, "회원가입 실패. 개발자에게 문의하세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}