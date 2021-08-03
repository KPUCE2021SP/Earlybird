package com.earlybird.runningbuddy

import android.Manifest.permission.SET_TIME
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class TImer2: AppCompatActivity()  {
    private lateinit var binding: ActivityMainBinding

    var started = false //타이머가 진행중임을 알려주는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonStart2.setOnClickListener {
            start()
        }
        binding.buttonStop2.setOnClickListener {
            stop()
        }
        binding.buttonPause2.setOnClickListener{
            pause()
        }
    }

    val SET_TIME = 51
    val RESET = 52

    val handler = object: Handler() { //sub thread
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                SET_TIME -> {
                    binding.textTimer2.text = formatTime(msg.arg1)
                }
                RESET -> {
                    binding.textTimer2.text = "00.00"
                }
            }
        }
    }


    fun start(){
        started = true // 타이머가 진행중인지 우리가 인식하려는 것
        //sub thread
        thread(start=true) { //쓰레드에게 진행중인지 말해주려는 것
            var total = 0
            while (true) {
                Thread.sleep(1000)
                if(!started) break
                total = total + 1

                val msg = Message()
                msg.what = SET_TIME
                msg.arg1 =total
              handler.sendMessage(msg)
            }
        }
    }

    fun pause() {
        started = false

    }

    fun stop() {
        started = false
        binding.textTimer2.text = "00 : 00"

    }

    fun formatTime(time:Int) : String {
        val minute = String.format("%02d", time/60)
        val second = String.format("%02d", time%60)
        return "$minute : $second"
    }

}