package com.earlybird.runningbuddy

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityLoginBinding
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding
import com.naver.maps.geometry.LatLng
import kotlin.math.roundToInt
import kotlin.collections.ArrayList


class RunningActivity : AppCompatActivity() {
    // 달리는 중인지 서비스에서 확인하기 위해
    companion object{
        var isRunning = true
    }

    private lateinit var binding: ActivityRunningBinding
    private lateinit var dataViewIntent: Intent    //DataViewActivity에 값을 주기 위한 intent
    private lateinit var serviceIntent: Intent //RunningService의 값을 받기 위한 intent

    private var time = 0.0
    private var timerstatus = true

    private var distance = 0.0
    private var pathList = ArrayList<LatLng>()

    private var isMap = false   // mapFragment

    private val intentFilter = IntentFilter()

    lateinit var mService: RunningService   //RunningService 에 접근하기 위한 변수
    var mBound: Boolean = false             //true 일 경우 서비스 실행 중

    private val connection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as RunningService.MyBinder
            mService = binder.getService()
            mBound = true
            // map fragment 를 한번만 그려주기 위해
            if(!isMap) {
                val transaction = supportFragmentManager.beginTransaction()
                    .add(R.id.map, MapFragment(this@RunningActivity))
                transaction.commit()
                isMap = true
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("HAN_RunningActivity","onServiceDisconnected()")
            mBound = false
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setIntent()

        setAction()

        setButton()
    }

    override fun onStart() {
        super.onStart()
        // 서비스가 중단되지 않게 하기 위해
        startService(serviceIntent)
        // 바인드된 서비스 실행, 서비스안에 있는 함수를 호출하기 위해
        Intent(this, RunningService::class.java).also { intent ->
            Log.d("HAN_RunningActivity","RunningActivity onStart()")
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        Log.d("serviceCycle", "RunningActivity onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.d("serviceCycle", "RunningActivity onStop()")
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("serviceCycle", "RunningActivity onDestroy()")
        unbindService(connection)
        mBound = false
        isRunning = false
    }

    // 뒤로 가기 막기, 일단 단순히 뒤로 못가게 막음
    override fun onBackPressed() {
        return
    }

    private fun setIntent(){
        serviceIntent = Intent(this, RunningService::class.java)
        dataViewIntent = Intent(this, DataViewActivity::class.java)

        serviceIntent.putExtra(RunningService.TIME_EXTRA,time)  //time값 RunningService로 보내기
        serviceIntent.putExtra(RunningService.DISTANCE_EXTRA,distance)
    }

    private fun setAction(){
        intentFilter.addAction("DistanceService")
        intentFilter.addAction("timerUpdated")
        intentFilter.addAction("PathListService")
        registerReceiver(RunningBroadCast(),intentFilter)
    }

    private fun setButton(){
        binding.stopButton.setOnClickListener {
            stopRunning()
            startActivity(dataViewIntent)
        }

        binding.pauseButton.setOnClickListener {
            isRunning = !isRunning
            Log.d("serviceCycle","isRunning : ${isRunning}")
            if (timerstatus)
                stopRunning()
            else
                restartRunning()
        }
    }

    private fun getTimeStringFromDouble(time: Double): String { //시간을 스트링으로 변환
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun restartRunning() {
        serviceIntent.putExtra(RunningService.TIME_EXTRA, time)
        val restart = true
        serviceIntent.putExtra("restart",restart)
        bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE)
        binding.pauseButton.text = "일시정지"
        timerstatus = true
        mBound = true
    }

    private fun stopRunning() {
        Log.d("serviceCycle","stopRunning()")
        unbindService(connection)
        binding.pauseButton.text = "재시작"
        timerstatus = false
        mBound = false
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = //문자 합치기
        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    inner class RunningBroadCast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                "timerUpdated" -> {
                    time = intent.getDoubleExtra(RunningService.TIME_EXTRA, 0.0)
                    binding.TimeView.text = getTimeStringFromDouble(time)
                }
                "DistanceService" -> {
                    Log.d("service22","BroadcastReceiver distanceService")
                    distance = intent.getDoubleExtra(RunningService.DISTANCE_EXTRA, 0.0)
                    binding.distanceView.text = "%.1f km".format(distance)
                }
                "PathListService"->{
                    pathList = intent.getParcelableArrayListExtra<LatLng>("pathList") as ArrayList<LatLng>
                    Log.d("service22","pathList : $pathList")
                }
            }
            binding.TimeView.text = getTimeStringFromDouble(time)
            binding.distanceView.text = "%.1f km".format(distance)
            Log.d("service22", "broadCast : $distance")
        }
    }
}