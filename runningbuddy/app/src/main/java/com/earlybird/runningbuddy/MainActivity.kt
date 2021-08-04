package com.earlybird.runningbuddy

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
                Start()
            } else {
                Pause()
            }
        }
    }

    private fun Pause() {
        binding.runButton.text = "시작"
        timerTask?.cancel() //
    }

    private fun Start() {
        binding.runButton.text = "종료"
        val timestart = TimeStart()

    }



    private fun TimeStart() {
        timerTask = timer(period = 10, initialDelay = 1000) {  //주기 : 1초, 초기딜레이시간 1초
            time++
            sec = time % 60
            min = (time / 60) % 60
            hour = time / 3600

            runOnUiThread {
                binding.hourView.text = "${hour}"
                binding.minuteView.text = "${min}"
                binding.secondView.text = "${sec}"
            }

        }
    }
}