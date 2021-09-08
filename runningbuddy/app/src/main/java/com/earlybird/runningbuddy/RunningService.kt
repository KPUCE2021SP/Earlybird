package com.earlybird.runningbuddy

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class RunningService : Service() {

    // 시간 계산을 위한 변수
    private val timer = Timer() //timer객체
    var currentTime = 0.0
    private var timePerDistance = 0.0
    // pace 를 위한 변수
    private var lastDistance = 0
    private var currentDistance = -1
    private var compareTime: Int = 0

    //    private var pace = 0.0
    private var pacearray = mutableListOf<Double>()
    private var temporalDistanceArray = mutableListOf<Double>()

    companion object {   //단순 시간저장공간
        const val TIMER_UPDATED = "timerUpdated"    //전송될 값
        const val TIME_EXTRA = "timeExtra"  //전송 키

        const val DISTANCE_UPDATED = "DistanceService"
        const val DISTANCE_EXTRA = "distance"

        const val PATH_UPDATED = "PathListService"
        const val PATH_EXTRA = "pathList"

        const val PACE_UPDATE = "paceUpdated"
        const val PACE_EXTRA = "paceExtra"

        const val TIMEPERDISTANCE_UPDATE = "timePerDistancUpdate"
        const val TIMEPERDISTANCE_EXTRA = "timePerDistanceExtra"
    }

    private var tts: TextToSpeech? = null

//     Dispatchers.Main : 기본 Android 스레드에서 코투린을 실행
//     UI와 상호작용하고 빠른 작업을 실행하기 위해서만 사용해야함
//    private val timerThread = CoroutineScope(Dispatchers.Main)  // timer 코루틴을 위한 객체
//    private val timerIntent = Intent()  // timer 정보를 전달하기 위한 intent 객체

    private var distance: Double = 0.0
    private val mapThread = CoroutineScope(Dispatchers.Main)
    private val distanceIntent = Intent(DISTANCE_UPDATED)   // 거리 정보를 전달하기 위한 intent
    private val pathList = ArrayList<LatLng>()  // 경로 저장하기 위한 리스트
    private val pathListIntent = Intent(PATH_UPDATED)   // 경로 저장 리스트를 전달하기 위한
    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap  //naver 객체
    private val binder = MyBinder()


    // 종료시 리스너 삭제를 위해
    private val changeLocation = object : NaverMap.OnLocationChangeListener {
        override fun onLocationChange(location: Location) {
            Log.d("service333", "locationChange()")

            if (pathList.size < 2) {// 2개 이상 가지고 있어야 하므로
                pathList.add(LatLng(location.latitude, location.longitude))
                pathList.add(LatLng(location.latitude, location.longitude))
            } else {
                pathList.add(LatLng(location.latitude, location.longitude))
            }
            drawPath()
            // 일시정지를 눌렀을 경우 경로는 그리되 거리는 증가하지 않도록 하기 위해
            if (RunningActivity.mBound) {
                pathListIntent.putExtra(PATH_EXTRA, pathList)
                sendBroadcast(pathListIntent)
                setDistance()
                calculatePace()
                saveTemporalDistance()
            }
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }

    }

    inner class MyBinder : Binder() {
        //  클라이언트가 서비스와 상호작용하는 데 사용할 수 있는 프로그래밍 인터페이스를 정의하는 IBinder 객체를 반환
        fun getService(): RunningService {
            Log.d("serviceCycle", "RunningService binder()")
            return this@RunningService
        }
    }

    override fun onCreate() {
        //runningActivity.isMap
        Log.d("serviceCycle", "onCreate()")

        // runningBuddy 로 실행시
        initTextToSpeech()


    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("service333", "onBind()")

        val time = intent.getDoubleExtra(TIME_EXTRA, 0.0)    //TIME_EXTRA 0.0으로 초기화
        timer.scheduleAtFixedRate(
            TimeTask(time),
            1000,
            1000
        ) //일정한 시간(delay)이 지난후에 일정 간격(period)으로 지정한 작업(task)을 수행
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("serviceCycle", "onUnbind()")
        return false
    }

    // 서비스가 수신하는 마지막 호출
    override fun onDestroy() {
        Log.d("service333", "destroy()")
        Log.d("serviceCycle", "service : onDestroy()")
        //naverMap.removeOnLocationChangeListener(changeLocation)
        mapThread.cancel()
        timer.cancel()
    }

    // mapFragment 에 그려진 지도 설정
    fun setNaverMap(naverMap: NaverMap, path: PathOverlay) {
        Log.d("serviceCycle", "setNaverMap")
        this.naverMap = naverMap
        this.path = path


        Log.d("serviceCycle", "${naverMap.locationSource}")
        locationChange()

    }


    private fun locationChange() = mapThread.launch {
        naverMap.addOnLocationChangeListener(changeLocation)
    }

    // km 로 변환하여 전달
    private fun setDistance() {
        val mDistance = pathList[pathList.size - 1].distanceTo(pathList[pathList.size - 2])
        this.distance += mDistance / 1000f


        distanceIntent.putExtra(DISTANCE_EXTRA, distance)
        sendBroadcast(distanceIntent)
    }

    // tts 관련
    private fun initTextToSpeech() {
        // 버전 확인 롤리팝 이상이여야 TTS 사용 가능
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(this, "SDK version is low", Toast.LENGTH_SHORT).show()
            return
        }
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                    return@TextToSpeech
                }
                Toast.makeText(this, "TTS setting successed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "TTS init failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ttsSpeak(strTTS: String) {
        tts?.speak(strTTS, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // 지도에 경로 그리기
    private fun drawPath() {
        path.coords = pathList
        path.map = naverMap
    }


    private inner class TimeTask(private var time: Double) : TimerTask() {   //시간 작업(task)
        override fun run() {
            val intent = Intent(TIMER_UPDATED)  //전송될 값 intent
            if (RunningActivity.mBound) {
                time++
                currentTime = time
            }
            intent.putExtra(TIME_EXTRA, time)    //time값 TIMER_UPDATED로 넘기기
            sendBroadcast(intent)   //TIMER_UPDATED 브로드캐스트로 넘기기
            if (time >= 60 && (time % 60 == 0.0)) {
                // 1 분 단위로
                val setTime: Int = (time / 60).toInt()
                alertAlarmWithTTS(setTime)
            }
        }

        private fun alertAlarmWithTTS(time: Int) {
            ttsSpeak("$time 분 경과 했습니다.")

        }
    }

    private fun calculatePace() {
        currentDistance = distance.toInt()
        var pace = 0 // 초
        if (lastDistance == currentDistance) { //같은 km
            return
        } else { // 1km마다
            pace = (currentTime - compareTime).toInt() //페이스를 Int형 초단위로 지정
            val intent = Intent(PACE_UPDATE)
            intent.putExtra(PACE_EXTRA, pace)
            sendBroadcast(intent)
            lastDistance = currentDistance
            compareTime=currentTime.toInt()
        }
    }

    private fun saveTemporalDistance(){

        if(currentTime % 60 == 0.0) {
//            temporalDistanceArray.add(distance)
            val intent = Intent(TIMEPERDISTANCE_UPDATE)
            intent.putExtra(TIMEPERDISTANCE_EXTRA,currentDistance)
            sendBroadcast(intent)
        }

    }
}