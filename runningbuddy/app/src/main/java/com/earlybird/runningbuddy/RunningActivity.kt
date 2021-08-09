package com.earlybird.runningbuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding

class RunningActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRunningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = supportFragmentManager.beginTransaction().add(R.id.map,MapFragment(true))
        transaction.commit()
        Log.d("Map22","runningActivity")

        binding.stopButton.setOnClickListener { //stopButton클릭 시
            val intent = Intent(this,DataViewActivity::class.java)  //DataViewActivity로 넘어감
            startActivity(intent)
        }
    }
}