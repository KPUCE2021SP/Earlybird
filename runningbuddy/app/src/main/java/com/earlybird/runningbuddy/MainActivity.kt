package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import java.lang.reflect.Array.get
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val timer = MyTimer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.runButton.setOnClickListener {
            timer.isRunning =! timer.isRunning

            if (timer.isRunning) {
                timer.Start()
            } else {
                timer.Pause()
            }
        }
    }


}