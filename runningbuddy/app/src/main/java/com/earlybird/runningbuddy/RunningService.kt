package com.earlybird.runningbuddy

import android.app.Service
import android.content.Intent
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

class RunningService : Service() {

    // 시간 계산을 위한 변수
    private var min: Int = 0
    private var sec:Int = 0
    private var hour:Int = 0

    // Dispatchers.Main : 기본 Android 스레드에서 코투린을 실행
    // UI와 상호작용하고 빠른 작업을 실행하기 위해서만 사용해야함
    private val timerThread = CoroutineScope(Dispatchers.Main)  // timer 코루틴을 위한 객체
    private val timerIntent = Intent()  // timer 정보를 전달하기 위한 intnet 객체

    private var dicstance:Double = 0.0
    private val mapThread = CoroutineScope(Dispatchers.Main)
    private val distanceIntent = Intent()   // 거리 정보를 전달하기 위한 intent
    private val pathList = mutableListOf<LatLng>()  // 경로 저장하기 위한 리스트
    private val pathListIntent = Intent()   // 경로 저장 리스트를 전달하기 위한
    private lateinit var path:PathOverlay
    private lateinit var naverMap:NaverMap

    //  클라이언트가 서비스와 상호작용하는 데 사용할 수 있는 프로그래밍 인터페이스를 정의하는 IBinder 객체를 반환
    override fun onBind(intent: Intent): IBinder {
        // 메서드를 항상 구현해야 하기에 일단 null 반환
        val binder:IBinder? = null
        return binder!!
    }

    fun setNaverMap(naverMap: NaverMap,path:PathOverlay){
        Log.d("service22","setnaverMap")
        this.naverMap=naverMap
        this.path = path
        launchMap()
    }

    private fun launchMap() = mapThread.launch {
        Log.d("service22","launchMap")
        naverMap.addOnLocationChangeListener {
            if (pathList.size < 2) {// 2개 이상 가지고 있어야 하므로
                pathList.add(LatLng(it.latitude, it.longitude))
                pathList.add(LatLng(it.latitude, it.longitude))
            } else {
                pathList.add(LatLng(it.latitude, it.longitude))
            }
            path.coords = pathList

            path.map = naverMap
        }

    }
    // 서비스가 수신하는 마지막 호출
    override fun onDestroy(){
        mapThread.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}