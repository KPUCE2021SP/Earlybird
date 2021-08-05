package com.earlybird.runningbuddy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityLoginBinding
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding

class RunningActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRunningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.stopButton.setOnClickListener { //stopButton클릭 시
            val intent = Intent(this,DataViewActivity::class.java)  //DataViewActivity로 넘어감
            startActivity(intent)
        }
    }
}