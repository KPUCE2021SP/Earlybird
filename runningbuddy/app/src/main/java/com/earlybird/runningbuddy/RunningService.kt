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
import java.util.*
import kotlin.concurrent.timer

class RunningService : Service() {

<<<<<<< Updated upstream
    private val timer = Timer() //timer객체

    companion object{   //단순 시간저장공간
        const val TIMER_UPDATED = "timerUpdated"    //전송될 값
        const val TIME_EXTRA = "timeExtra"  //전송 키
    }
=======
//    private val CHANNEL_ID = "ForegroundChannel"    //알림에 사용될 채널
//    // 시간 계산을 위한 변수
//    private var min: Int = 0
//    private var sec: Int = 0
//    private var hour: Int = 0
//    private var timerTask: Timer? = null   //timer활성
//    private var time = 0            //기본 시간
>>>>>>> Stashed changes

//     Dispatchers.Main : 기본 Android 스레드에서 코투린을 실행
//     UI와 상호작용하고 빠른 작업을 실행하기 위해서만 사용해야함
//    private val timerThread = CoroutineScope(Dispatchers.Main)  // timer 코루틴을 위한 객체
//    private val timerIntent = Intent()  // timer 정보를 전달하기 위한 intnet 객체

    private var dicstance: Double = 0.0
    private val mapThread = CoroutineScope(Dispatchers.Main)
    private val distanceIntent = Intent()   // 거리 정보를 전달하기 위한 intent
    private val pathList = mutableListOf<LatLng>()  // 경로 저장하기 위한 리스트
    private val pathListIntent = Intent()   // 경로 저장 리스트를 전달하기 위한
    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap



    //  클라이언트가 서비스와 상호작용하는 데 사용할 수 있는 프로그래밍 인터페이스를 정의하는 IBinder 객체를 반환
    override fun onBind(intent: Intent): IBinder {
        // 메서드를 항상 구현해야 하기에 일단 null 반환
        val binder: IBinder? = null
        return binder!!
    }

    fun setNaverMap(naverMap: NaverMap, path: PathOverlay) {
        Log.d("service22", "setnaverMap")
        this.naverMap = naverMap
        this.path = path
        launchMap()
    }

    private fun launchMap() = mapThread.launch {
        Log.d("service22", "launchMap")
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
    override fun onDestroy() {
        mapThread.cancel()
        timer.cancel()
    }

<<<<<<< Updated upstream
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {   //Started Service에서 서비스 시작시 호출
        val time = intent.getDoubleExtra(TIME_EXTRA,0.0)    //TIME_EXTRA 0.0으로 초기화
        timer.scheduleAtFixedRate(TimeTask(time),1000,1000) //일정한 시간(delay)이 지난후에 일정 간격(period)으로 지정한 작업(task)을 수행
        return START_NOT_STICKY
    }

    private inner class TimeTask (private var time: Double): TimerTask(){   //시간 작업(task)
        override fun run(){
            val intent = Intent(TIMER_UPDATED)  //전송될 값 intent
            time++
            intent.putExtra(TIME_EXTRA,time)    //time값 TIMER_UPDATED로 넘기기
            sendBroadcast(intent)   //TIMER_UPDATED 브로드캐스트로 넘기기
        }
    }


}
=======
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {   //Started Service에서 서비스 시작시 호출
//        timerTask = timer(period = 10, initialDelay = 1000) {  //주기 : 1초, 초기딜레이시간 1초
//                time++
//                sec = time % 60
//                min = (time / 60) % 60
//                hour = time / 3600
//
//           }
//        return START_STICKY //서비스 강제 종료 시 intnet값을 null로 초기화 시켜 서비스 재시작시켜줌
//    }
}
>>>>>>> Stashed changes
