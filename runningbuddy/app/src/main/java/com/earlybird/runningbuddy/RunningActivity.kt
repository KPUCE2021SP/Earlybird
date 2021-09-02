package com.earlybird.runningbuddy

import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.RunningService.Companion.DISTANCE_EXTRA
import com.earlybird.runningbuddy.RunningService.Companion.TIME_EXTRA
import com.earlybird.runningbuddy.databinding.ActivityLoginBinding
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding
import com.google.android.gms.common.internal.Objects
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import kotlin.collections.ArrayList


class RunningActivity : AppCompatActivity() {
    // 달리는 중인지 서비스에서 확인하기 위해
    companion object {
        var mBound: Boolean = false             //true 일 경우 서비스 실행 중
    }

    private lateinit var binding: ActivityRunningBinding
    private lateinit var dataViewIntent: Intent    //DataViewActivity에 값을 주기 위한 intent
    private lateinit var serviceIntent: Intent //RunningService의 값을 받기 위한 intent


    private var pace = 0.0
    private var time = 0.0
    private var pacearray = mutableListOf<Double>()

    private var distance = 0.0
    private var pathList = ArrayList<LatLng>()

    private var isMap = false   // mapFragment

    private val intentFilter = IntentFilter()

    lateinit var mService: RunningService   //RunningService 에 접근하기 위한 변수

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as RunningService.MyBinder
            mService = binder.getService()
            mBound = true
            // map fragment 를 한번만 그려주기 위해
            if (!isMap) {
                val transaction = supportFragmentManager.beginTransaction()
                    .add(R.id.map, MapFragment(this@RunningActivity))
                transaction.commit()
                isMap = true
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("HAN_RunningActivity", "onServiceDisconnected()")
            mBound = false
        }
    }

    private var tts: TextToSpeech? = null

    @RequiresApi(Build.VERSION_CODES.O) // 현재시간을 표시하는 LocalDateTime.now() 함수를 쓰러면 이 코드를 추가해야만함
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunningBinding.inflate(layoutInflater)
        binding.paceView.text = calculatePace()
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
            Log.d("HAN_RunningActivity", "RunningActivity onStart()")
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
    }

    // 뒤로 가기 막기, 일단 단순히 뒤로 못가게 막음
    override fun onBackPressed() {
        return
    }

    private fun setIntent() {
        serviceIntent = Intent(this, RunningService::class.java)
        dataViewIntent = Intent(this, DataViewActivity::class.java)

        serviceIntent.putExtra(RunningService.TIME_EXTRA, time)  //time값 RunningService로 보내기
        serviceIntent.putExtra(RunningService.DISTANCE_EXTRA, distance)
    }

    private fun setAction() {
        intentFilter.addAction("DistanceService")
        intentFilter.addAction("timerUpdated")
        intentFilter.addAction("PathListService")
        registerReceiver(RunningBroadCast(), intentFilter)
    }

    @RequiresApi(Build.VERSION_CODES.O) // 현재시간을 표시하는 LocalDateTime.now() 함수를 쓰러면 이 코드를 추가해야만함
    private fun setButton() {
        binding.stopButton.setOnClickListener {
            stopRunning() // 러닝 종료버튼

            //db에 접근하기위해 forestore 객체 할당
            val db: FirebaseFirestore = Firebase.firestore

            //현재 시간을 불러오는 LocalDateTime.now() 함수를 사용, 원하는 문자열 양식으로 포맷팅 한뒤 formatedDate 변수에 할당
            val currentDate = LocalDateTime.now()
            val formatedDate: String = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)!!

            //기록중 시간과 거리(path는 아직 미구현)를 map 형태의 자료구조로 담아줌
            val currentRecordMap = hashMapOf(
                "Time" to time,
                "Distance" to distance,
                "PathList" to pathList,
                "Date" to formatedDate,
                "UserID" to Firebase.auth.currentUser!!.uid
            )

            //회원가입때와 달라진점 = .set 뒤에가 달라짐. 회원정보는 한 회원당 하나만 존재 하니까 "db에 덮어씌우고"
            // 러닝 기록은 회원마다 여러개니까 "db에 기존 기록이 있건없건 빈 공간에 merge 함"
            db.collection("records")
                .add(currentRecordMap)

            // 데이터 뷰에 보이는 것은 db에서 가져오는 것보다 인텐트로 하는 것이 더 효율적이라 판단하여 인텐트로 데이터 전달
            dataViewIntent.putExtra("Time", time)        // 칼로리 계산을 위해
            dataViewIntent.putExtra("FormatTime", binding.TimeView.text)         // 달린 시간을 보여주기 위해
            dataViewIntent.putExtra("Distance", distance)
            startActivity(dataViewIntent)
        }

        binding.pauseButton.setOnClickListener {

            if (mBound)
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
        serviceIntent.putExtra("restart", restart)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        binding.pauseButton.text = "일시정지"
        mBound = true
    }

    private fun stopRunning() {
        Log.d("serviceCycle", "stopRunning()")
        unbindService(connection)
        stopService(serviceIntent)
        binding.pauseButton.text = "재시작"
        mBound = false
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


    private fun ttsSpeak(strTTS:String){
        tts?.speak(strTTS,TextToSpeech.QUEUE_FLUSH,null,null)
    }


    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = //문자 합치기
        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    inner class RunningBroadCast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                "timerUpdated" -> {
                    time = intent.getDoubleExtra(RunningService.TIME_EXTRA, 0.0) //시간표시

                    binding.TimeView.text = getTimeStringFromDouble(time)
                }
                "DistanceService" -> {
                    Log.d("service22", "BroadcastReceiver distanceService")
                    distance = intent.getDoubleExtra(RunningService.DISTANCE_EXTRA, 0.0) //거리표시
                    binding.distanceView.text = "%.1f km".format(distance)
                }
                "PathListService" -> {
                    pathList =
                        intent.getParcelableArrayListExtra<LatLng>("pathList") as ArrayList<LatLng> //구간좌표 표시
                    Log.d("service22", "pathList : $pathList")
                }
            }
            Log.d("service22", "broadCast : $distance")
        }
    }


    private fun calculatePace(): String {
        var time = intent.getDoubleExtra(TIME_EXTRA, 0.0)
        var distance = intent.getDoubleExtra(DISTANCE_EXTRA, 0.0)
        var pacesize: Int = pacearray.size

        // 1km마다 시간을 배열에 저장
        if(distance % 1 == 0.0){
            pacearray.add(time)
        }

        if(distance == 1.0){
            pace = distance
        } else if(pacesize != 0) {
            if (distance % 1 == 0.0) {
                pace = time - pacearray.get(pacesize - 1)
            }
        }

        return String.format("%.1f km/m", pace)
    }
}