package com.earlybird.runningbuddy

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import kotlin.math.round
import kotlin.math.roundToInt

class RunningService : Service() {

    // 시간 계산을 위한 변수
    private var min: Int = 0
    private var sec: Int = 0
    private var hour: Int = 0

    // Dispatchers.Main : 기본 Android 스레드에서 코투린을 실행
    // UI와 상호작용하고 빠른 작업을 실행하기 위해서만 사용해야함
    private val timerThread = CoroutineScope(Dispatchers.Main)  // timer 코루틴을 위한 객체
    private val timerIntent = Intent()  // timer 정보를 전달하기 위한 intnet 객체

    private var distance: Double = 0.0
    private val mapThread = CoroutineScope(Dispatchers.Main)
    private val distanceIntent = Intent("DistanceService")   // 거리 정보를 전달하기 위한 intent
    private val pathList = mutableListOf<LatLng>()  // 경로 저장하기 위한 리스트
    private val pathListIntent = Intent()   // 경로 저장 리스트를 전달하기 위한
    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap  //naver 객체

    override fun onCreate() {
        Log.d("serviceCycle", "onCreate()")
        timerIntent.action
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("serviceCycle", "onStartCommand()")
        return START_NOT_STICKY
    }

    //  클라이언트가 서비스와 상호작용하는 데 사용할 수 있는 프로그래밍 인터페이스를 정의하는 IBinder 객체를 반환
    override fun onBind(intent: Intent): IBinder {
        Log.d("serviceCycle", "onBind()")
        // 메서드를 항상 구현해야 하기에 일단 null 반환
        val binder: IBinder? = null

        return binder!!
    }

    fun setNaverMap(naverMap: NaverMap, path: PathOverlay, flag: Boolean) {
        Log.d("service22", "setNaverMap")
        this.naverMap = naverMap
        this.path = path
        launchMap(flag)
    }

    private fun launchMap(flag: Boolean) = mapThread.launch {
        Log.d("service22", "launchMap")
        naverMap.addOnLocationChangeListener {
            if (pathList.size < 2) {// 2개 이상 가지고 있어야 하므로
                pathList.add(LatLng(it.latitude, it.longitude))
                pathList.add(LatLng(it.latitude, it.longitude))
            } else {
                pathList.add(LatLng(it.latitude, it.longitude))
            }
            Log.d("flag", "flag : $flag")

            if (flag) {
                drawPath()
                val distance = pathList[pathList.size - 1].distanceTo(pathList[pathList.size - 2])
                setDistance(distance)
                Log.d("service22", "distance : $distance")
            }
        }
    }

    private fun setDistance(distance:Double){
        this.distance += distance/1000f
        Log.d("service22","소수점 1자리까지만 this.distance : %.1f".format(this.distance))
        Log.d("service22","this.distance : ${this.distance}")
        //distanceIntent.action = "DistanceService"
        distanceIntent.putExtra("distance",this.distance)
        Log.d("service22","sendBroadcast")
        Log.d("service22","distanceIntent.action : ${distanceIntent.action}")
        sendBroadcast(distanceIntent)
    }

    private fun drawPath() {
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
    }

}

