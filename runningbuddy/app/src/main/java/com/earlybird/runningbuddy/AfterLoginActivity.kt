package com.earlybird.runningbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.earlybird.runningbuddy.databinding.ActivityAfterLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AfterLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAfterLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Firebase.auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
        binding.textUID.text = Firebase.auth.currentUser?.uid ?: "No User"
        binding.buttonSignout.setOnClickListener {
            Firebase.auth.signOut()
            finish()
        }
        binding.buttonMap.setOnClickListener {
            startActivity(
                Intent(this, MapActivity::class.java)
            )
        }
    }

}