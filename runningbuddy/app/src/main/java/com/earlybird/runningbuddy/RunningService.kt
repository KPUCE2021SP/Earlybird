package com.earlybird.runningbuddy

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.*
import kotlin.concurrent.timer

class RunningService : Service() {

    // 조깅 중인가
    private var isRunning = false

    // 시간 계산을 위한 변수
    private var min: Int = 0
    private var sec: Int = 0
    private var hour: Int = 0
    private val timer = Timer() //timer객체

    companion object {   //단순 시간저장공간
        const val TIMER_UPDATED = "timerUpdated"    //전송될 값
        const val TIME_EXTRA = "timeExtra"  //전송 키

        const val DISTANCE_UPDATED = "DistanceService"
        const val DISTANCE_EXTRA = "distance"
    }

//     Dispatchers.Main : 기본 Android 스레드에서 코투린을 실행
//     UI와 상호작용하고 빠른 작업을 실행하기 위해서만 사용해야함
//    private val timerThread = CoroutineScope(Dispatchers.Main)  // timer 코루틴을 위한 객체
//    private val timerIntent = Intent()  // timer 정보를 전달하기 위한 intnet 객체

    private var distance: Double = 0.0
    private val mapThread = CoroutineScope(Dispatchers.Main)
    private val distanceIntent = Intent("DistanceService")   // 거리 정보를 전달하기 위한 intent
    private val pathList = mutableListOf<LatLng>()  // 경로 저장하기 위한 리스트
    private val pathListIntent = Intent()   // 경로 저장 리스트를 전달하기 위한
    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap  //naver 객체
    private val binder = MyBinder()

    inner class MyBinder : Binder() {//  클라이언트가 서비스와 상호작용하는 데 사용할 수 있는 프로그래밍 인터페이스를 정의하는 IBinder 객체를 반환
        fun getService(): RunningService {
            Log.d("Map22","RunningService binder()")
            return this@RunningService
        }
    }

    override fun onCreate() {
        Log.d("serviceCycle", "onCreate()")
        timerIntent.action
    }
    override fun onBind(intent: Intent): IBinder {
        Log.d("Map22", "onBind()")
        return binder
    }

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {   //Started Service에서 서비스 시작시 호출
        val time = intent.getDoubleExtra(TIME_EXTRA, 0.0)    //TIME_EXTRA 0.0으로 초기화
        timer.scheduleAtFixedRate(
            TimeTask(time),
            1000,
            1000
        ) //일정한 시간(delay)이 지난후에 일정 간격(period)으로 지정한 작업(task)을 수행
        Log.d("Map22","RunningService onStartCommand()")
        return START_NOT_STICKY
    }

    private fun setIntentAction(){

    }

    // mapFragment 에 그려진 지도 설정
    fun setNaverMap(naverMap: NaverMap, path: PathOverlay) {
        Log.d("service", "setNaverMap")
        this.naverMap = naverMap
        this.path = path
        launchMap()
    }

    // 좌표 저장
    private fun launchMap() = mapThread.launch {
        Log.d("service22", "launchMap")
        naverMap.addOnLocationChangeListener {
            if (pathList.size < 2) {// 2개 이상 가지고 있어야 하므로
                pathList.add(LatLng(it.latitude, it.longitude))
                pathList.add(LatLng(it.latitude, it.longitude))
            } else {
                pathList.add(LatLng(it.latitude, it.longitude))
            }
            drawPath()
            val distance = pathList[pathList.size - 1].distanceTo(pathList[pathList.size - 2])
            setDistance(distance)
            //Log.d("service22", "distance : $distance")
        }
    }

    // km 로 변환하여 전달
    private fun setDistance(mDistance: Double) {
        this.distance += mDistance / 1000f
        //Log.d("service22", "소수점 1자리까지만 this.distance : %.1f".format(distance))
        Log.d("service22", "this.distance : ${distance}")

        distanceIntent.putExtra("distance", distance)

        sendBroadcast(distanceIntent)
    }

    // 지도에 경로 그리기
    private fun drawPath() {
        Log.d("serviceCycle", "drawPath()")
        path.coords = pathList
        path.map = naverMap
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("serviceCycle", "onUnbind()")
        return super.onUnbind(intent)
    }

    // 서비스가 수신하는 마지막 호출
    override fun onDestroy() {
        Log.d("serviceCycle", "onDestroy()")
        mapThread.cancel()
        timer.cancel()
    }


    private inner class TimeTask(private var time: Double) : TimerTask() {   //시간 작업(task)
        override fun run() {
            val intent = Intent(TIMER_UPDATED)  //전송될 값 intent
            time++
            intent.putExtra(TIME_EXTRA, time)    //time값 TIMER_UPDATED로 넘기기
            sendBroadcast(intent)   //TIMER_UPDATED 브로드캐스트로 넘기기
        }
    }
}

