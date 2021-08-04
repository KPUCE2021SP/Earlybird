package com.earlybird.runningbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var lap = 1
    private var sec: Int = 0
    private var min: Int = 0
    private var hour: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.runButton.setOnClickListener {
            isRunning = !isRunning

            if (isRunning) {
                RunStart()
            } else {
                RunPause()
            }
        }
    }

    private fun RunPause() {
        timerTask?.cancel()
    }

    private fun RunStart() {
        val timestart = TimeStart()
        val RunStartIntent = Intent(this@MainActivity,R.layout.activity_running::class.java)
        startActivity(RunStartIntent)

    }



    private fun TimeStart() { //타이머 돌아가는 함수
        timerTask = timer(period = 10, initialDelay = 1000) {  //주기 : 1초, 초기딜레이시간 1초
            time++
            sec = time % 60
            min = (time / 60) % 60
            hour = time / 3600
        }
    }
}