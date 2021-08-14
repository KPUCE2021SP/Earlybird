package com.earlybird.runningbuddy

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityLoginBinding
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding
import kotlin.math.roundToInt

class RunningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRunningBinding
    private lateinit var dataViewIntent: Intent    //DataViewActivity에 값을 주기 위한 intent
    private lateinit var serviceIntent: Intent //RunningService의 값을 받기 위한 intent

    private var time = 0.0

    private var distance = 0.0

//    private val br = MyBroadcastReceiver()
    private val intentFilter = IntentFilter()

    lateinit var mService: RunningService
    var mBound: Boolean = false

    private val connection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d("Map22","onServiceConnected()")
            val binder = service as RunningService.MyBinder
            mService = binder.getService()
            mBound = true
            val transaction = supportFragmentManager.beginTransaction().add(R.id.map, MapFragment(this@RunningActivity))
            transaction.commit()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("Map22","onServiceDisconnected()")
            mBound = false
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(this, RunningService::class.java)
        dataViewIntent = Intent(this, DataViewActivity::class.java)

        serviceIntent.putExtra(RunningService.TIME_EXTRA,time)  //time값 RunningService로 보내기
        startService(serviceIntent)

        Intent(this, RunningService::class.java).also { intent ->
            Log.d("Map22","RunningActivity onStart()")
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        intentFilter.addAction("DistanceService")
        intentFilter.addAction("timerUpdated")
        registerReceiver(RunningBroadCast(),intentFilter)


        binding.stopButton.setOnClickListener { //stopButton클릭 시
            stopService(serviceIntent)
            startActivity(dataViewIntent)
        }

        binding.pauseButton.setOnClickListener {
            pauseTime()
        }


        // MapFragment
        Log.d("Map22","runningActivity")


    }

    override fun onStart() {
        super.onStart()
//        Intent(this, RunningService::class.java).also { intent ->
//            Log.d("Map22","RunningActivity onStart()")
//            bindService(intent, connection, Context.BIND_AUTO_CREATE)
//        }
    }

    override fun onPause() {
        Log.d("Map22","RunningActivity onPause()")
        super.onPause()

    }

    override fun onStop() {
//        Log.d("Map22","RunningActivity onStop()")
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    inner class RunningBroadCast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                "timerUpdated" -> {
                    time = intent.getDoubleExtra(RunningService.TIME_EXTRA, 0.0)
                }
                "DistanceService" -> {
                    Log.d("service22","BroadcastReceiver distanceService")
                    distance = intent.getDoubleExtra(RunningService.DISTANCE_EXTRA, -1.0)
                }
            }
            //time = intent.getDoubleExtra(RunningService.TIME_EXTRA, 0.0)
            binding.TimeView.text = getTimeStringFromDouble(time)
            binding.distanceView.text = "%.1f km".format(distance)
            Log.d("service22", "broadCast : $distance")
        }
    }

    private fun getTimeStringFromDouble(time: Double): String { //시간을 스트링으로 변환
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = //문자 합치기
        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    private fun pauseTime() {
        if (timerstatus)
            stopTimer()
        else
            restartTimer()
    }

    private fun restartTimer() {
        serviceIntent.putExtra(RunningService.TIME_EXTRA, time)
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