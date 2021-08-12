package com.earlybird.runningbuddy

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timer

class RunningService2 : Service() {

    private var time = 0
    private var isRunning = false
    private var lap = 1
    private var sec: Int = 0
    private var min: Int = 0
    private var hour: Int = 0
    private var timerTask: Timer? = null
    private val timerThread = CoroutineScope(Dispatchers.Main)
    private val timer = Timer()



    override fun onBind(intent: Intent?): IBinder? = null

    private val br: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Receiver", "Intent: $intent")
        }
    }


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