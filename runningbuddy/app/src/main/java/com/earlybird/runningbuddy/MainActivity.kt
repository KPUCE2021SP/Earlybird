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
        timerTask?.cancel()
    }

    private fun Start() {
        binding.runButton.text = "종료"

        timerTask = timer(period = 10) {
            time++
            val milli = time % 100
            var sec = time / 100
            var min = (time / 100) / 60
            val hour = (time / 100) / 3600

            if (sec == 0) sec = time / 100
            else if (sec / 60 >= 1) sec %= 60

            if (min == 0) min = (time / 100) / 60
            else if (min / 60 >= 1) min %= 60

            runOnUiThread {
                binding.hourView.text = "${hour}"
                binding.minuteView.text = "${min}"
                binding.secondView.text = "${sec}"
                binding.milliView.text = "${milli}"
            }
        }
    }
}