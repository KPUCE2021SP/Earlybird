package com.example.testtimer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

class TimerService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    private val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getDoubleExtra(TIME_EXTRA,0.0)
        timer.scheduleAtFixedRate(TimeTask(time),0,1000)
        return START_NOT_STICKY
    }

    private inner class TimeTask(private var time: Double) : TimerTask()
    {
        override fun run(){
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
    companion  object {     //저장공간
        const val TIMER_UPDATED = "timerUpdated"
        const val TIME_EXTRA = "timeExtra"
    }
}