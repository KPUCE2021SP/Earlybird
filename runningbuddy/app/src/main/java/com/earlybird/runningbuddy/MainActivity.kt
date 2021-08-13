package com.earlybird.runningbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent  //RunningService를 위한 intent
    private lateinit var Activityintent: Intent    //RunningActivity를 위한 intent

    private var time = 0.0  //시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, RunningService::class.java)   //RunningService와 intent
        Activityintent = Intent(applicationContext,RunningActivity::class.java) //RunningActivity와 intent

        binding.runButton.setOnClickListener { //runButton클릭 시
            startTimer()
        }
    }

    private fun startTimer() {
        serviceIntent.putExtra(RunningService.TIME_EXTRA,time)  //time값 RunningService로 보내기
        startService(serviceIntent)
        startActivity(Activityintent)
    }   
}


