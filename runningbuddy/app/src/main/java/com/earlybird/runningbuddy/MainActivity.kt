package com.earlybird.runningbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var binding1: ActivityRunningBinding


    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var lap = 1
    private var sec: Int = 0
    private var min: Int = 0
    private var hour: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val transaction = supportFragmentManager.beginTransaction().add(R.id.map,MapFragment())
        transaction.commit()


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.runButton.setOnClickListener { //runButton클릭 시

            isRunning = !isRunning

            if (isRunning) {
                Start()
            } else {
                Pause()
            }
        }
    }

    private fun Pause() {
        timerTask?.cancel() //
    }

    private fun Start() {
        val timestart = TimeStart()
        val intent = Intent(this, RunningActivity::class.java)  //RunningActivity로 넘어감
        startActivity(intent)
    }


    private fun TimeStart() {
        timerTask = timer(period = 10, initialDelay = 1000) {  //주기 : 1초, 초기딜레이시간 1초
            time++
            sec = time % 60
            min = (time / 60) % 60
            hour = time / 3600

        }
    }
}


