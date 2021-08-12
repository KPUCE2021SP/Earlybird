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
        this.serviceIntent = Intent(this, RunningService2::class.java)   //RunningService로 넘어감

        binding.runButton.setOnClickListener { //runButton클릭 시
            Start()
            var intent = Intent(this, RunningBroadcast::class.java)
            sendBroadcast(intent)
//            isRunning = !isRunning
//
//            if (isRunning) {
//                Start()
//            } else {
//                Pause()
//            }
        }
    }

    private fun startTimer() {
        serviceIntent.putExtra(RunningService.TIME_EXTRA,time)  //time값 RunningService로 보내기
        startService(serviceIntent)
        startActivity(Activityintent)
    }


//    private fun TimeStart() {
//        timerTask = timer(period = 10, initialDelay = 1000) {  //주기 : 1초, 초기딜레이시간 1초
//            time++
//            sec = time % 60
//            min = (time / 60) % 60
//            hour = time / 3600
//
//        }
//    }

}


