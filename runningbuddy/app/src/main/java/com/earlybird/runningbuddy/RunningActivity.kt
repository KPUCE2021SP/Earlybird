package com.earlybird.runningbuddy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityLoginBinding
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding
import kotlin.math.roundToInt

class RunningActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRunningBinding
    private lateinit var dataViewIntent : Intent    //DataViewActivity에 값을 주기 위한 intent
    private lateinit var serviceIntent : Intent //RunningService의 값을 받기 위한 intent

    private var time = 0.0
    private var timerstatus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, RunningService::class.java)
        dataViewIntent = Intent(applicationContext, DataViewActivity::class.java)
        registerReceiver(RunningBroadCast(), IntentFilter(RunningService.TIMER_UPDATED))

        binding.stopButton.setOnClickListener { //stopButton클릭 시
            stopTimer()
            startActivity(dataViewIntent)
        }

        binding.pauseButton.setOnClickListener{
            pauseTime()
        }
    }

    inner class RunningBroadCast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getDoubleExtra(RunningService.TIME_EXTRA, 0.0)
            binding.TimeView.text = getTimeStringFromDouble(time)
        }
    }

//    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            time = intent.getDoubleExtra(RunningService.TIME_EXTRA, 0.0)
//            binding.TimeView.text = getTimeStringFromDouble(time)
//        }
//    }

    private fun getTimeStringFromDouble(time: Double): String { //시간을 스트링으로 변환
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = //문자 합치기
        String.format("%02d:%02d:%02d",hours,minutes,seconds)

    private fun pauseTime() {
        if(timerstatus)
            stopTimer()
        else
            restartTimer()
    }

    private fun restartTimer() {
        serviceIntent.putExtra(RunningService.TIME_EXTRA,time)
        startService(serviceIntent)
        binding.pauseButton.text = "일시정지"
        timerstatus = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        binding.pauseButton.text = "재시작"
        timerstatus = false
    }
}