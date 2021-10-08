package com.earlybird.runningbuddy.activity

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.earlybird.runningbuddy.*
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent  //RunningService 를 위한 intent
    private lateinit var activityIntent: Intent    //RunningActivity 를 위한 intent
    private lateinit var recordListIntent: Intent
    private lateinit var adapterIntent: Intent

    private lateinit var transaction: FragmentTransaction

    // 날씨
    val num_of_rows = 60
    val page_no = 1
    val data_type = "JSON"
    var base_time = "1100"
    var base_data = "20211008"
    var nx = "55"
    var ny = "127"

    var obsrValue:Double = 0.0

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    inner class ApiObject{
        val retrofitService:WeatherInterface by lazy {
            retrofit.create(WeatherInterface::class.java)
        }
    }

    private fun weather(date:String, time:String){
        base_time = time
        base_data = date
        Log.d("api","time : $base_time")
        Log.d("api","date : $base_data")
        val apiObject = ApiObject()
        val call = apiObject.retrofitService.GetWeather(data_type,num_of_rows,page_no,base_data,base_time,nx, ny)
        call.enqueue(object:retrofit2.Callback<WEATHER>{

            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful){
                    Log.d("api",response.body().toString())
                    Log.d("api",response.body()!!.response.body.items.toString())
                    Log.d("api",response.body()!!.response.body.items.item[0].category)

                    val list = response.body()!!.response.body.items.item
                    Log.d("api","${list[5]}")
                    Log.d("api","${list[6]}")
                    Log.d("api","${list[7]}")
                    Log.d("api","${list[18]}")
                    Log.d("api","${list[24]}")
                    //18 -> SKY(구름)
                    list[18]
                    binding.weatherText.text = "오늘은 뛰기 좋은 날씨네요!"
                    if(list[18].fcstValue.toInt() >= 3){
                        binding.weatherImage.setImageResource(R.drawable.cloud)
                        binding.weatherText.text = "구름이 많아 뛰기 좋을 것 같아요!"
                    }
                    if(list[18].fcstValue.toInt() == 4){
                        binding.weatherLayout.setBackgroundResource(R.color.dark_orange)
                        binding.weatherText.text = "오늘은 흐리네요. 그래도 열심히 달려봐요!"
                    }

                    //24 -> T1H(온도)
                    binding.weather.text = "${list[24].fcstValue}°C"


                    //6 -> PTY(강수)
                    if(list[6].fcstValue.toInt() > 0){
                        binding.weatherImage.setImageResource(R.drawable.umbrella)
                        binding.weatherText.text = "오늘은 비가 와서 뛰기 힘들 것 같네요"
                    }
                }
            }

            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                Log.d("api","Failure: ${t.message!!}")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ab : ActionBar? = supportActionBar
        ab?.setTitle("러닝버디 홈")
        transaction = supportFragmentManager.beginTransaction().add(R.id.map, MapFragment())
        transaction.commit()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, RunningService::class.java)   //RunningService 와 intent
        activityIntent = Intent(this, RunningActivity::class.java) //RunningActivity 와 intent
        recordListIntent = Intent(this, RecordListActivity::class.java)

        startRunning()

        binding.signOutButton.setOnClickListener {

            val builder = AlertDialog.Builder(this)
                .apply {
                    setTitle("알림")
                    setMessage("로그아웃 하시겠습니까?")
                    setPositiveButton("네") { _, _ ->
                        FirebaseAuth.getInstance().signOut()
                        Handler().postDelayed({
                            ActivityCompat.finishAffinity(this@MainActivity)
                            System.runFinalization()
                            System.exit(0)
                        }, 1000)
                    }
                    setNegativeButton("아니요"){_,_,->
                        return@setNegativeButton
                    }
                    show()
                }
        }

        binding.userInfoButton.setOnClickListener {
            binding.userInfoButton.isEnabled = false

            Handler().postDelayed({
                startActivity(
                    Intent(this, UserInfo::class.java)
                )
            }, 1000)


        }
        // 날씨
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyyMMdd HHmm",Locale("ko","KR"))
        val nowdateFormat = SimpleDateFormat("MM월dd일 HH:mm", Locale("ko","KR"))

        val calendar = Calendar.getInstance()
        val time = calendar.time
        calendar.setTime(date)
        val nowFormatTime = nowdateFormat.format(time)
        val b = nowFormatTime.split(" ")
        binding.date.text = b[0]
        binding.time.text = b[1]


        calendar.add(Calendar.HOUR,-1)

        val formatTime = dateFormat.format(time)

        Log.d("api",formatTime)

        val a = formatTime.split(" ")
        weather(a[0],a[1])
//        val now = System.currentTimeMillis()
//        val now_date = Date(now)
//        val sdf = SimpleDateFormat("yyyyMMdd HHmm")
//        val getTime = sdf.format(now_date)
//        Log.d("api",getTime)
//
//        val a = getTime.split(" ").toMutableList()
//
//        var time = a[1].toInt()
//        var date = a[0].toInt()
//        time -= 10
//        if(time<0)
//        {
//            date-=1
//            if(date%100==99){
//                var calendar = Calendar.getInstance()
//                calendar.add(Calendar.DAY_OF_YEAR,-1)
//                var TimeToDate = calendar.time
//                a[0]=sdf.format(TimeToDate)
//            }
//            a[1]="2345"
//        }
//

//        binding.date.text = a[0]
//        binding.time.text = a[1]
//        weather(a[0], a[1])
    }

    override fun onStart() {
        super.onStart()
        binding.userInfoButton.isEnabled = true
    }

    override fun onBackPressed() {
        return
    }

    private fun startRunning() {
        binding.runButton.setOnClickListener { //runButton 클릭 시
            // 권한 부여
            //requestPermission()
//        // 권한 설정
            if (!requestPermissionRationale()) return@setOnClickListener

            //LocationManager
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            // gps 를 껏을 경우
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val builder = AlertDialog.Builder(this)
                    .apply {
                        setTitle("경고")
                        setMessage("GPS 가 꺼져있습니다. GPS 를 키시겠습니까?")
                        setPositiveButton("네") { _, _ ->
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                        setNegativeButton("아니오", DialogInterface.OnClickListener { _, _ ->
                            return@OnClickListener
                        })
                        show()
                    }
            }

            val builder = AlertDialog.Builder(this)
                .apply {
                    setTitle("모드 선택")
                    setMessage("어느 모드로 실행할까요?\n(첫 달리기는 일반으로 해야합니다.) ")
                    setPositiveButton("버디") { _, _ ->
                        isBuddy = !isBuddy
                        Log.d("isBuddy", "")
                        startActivity(recordListIntent)
                    }
                    setNegativeButton("일반") { _, _ ->
                        startActivity(activityIntent)
                    }
                    show()
                }
//            } else if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                == PackageManager.PERMISSION_GRANTED)
//                startActivity(activityIntent)

        }

//        binding.buddyButton.setOnClickListener {
//            isBuddy = !isBuddy
//            Log.d("isBuddy","")
//            recordListIntent = Intent(this, RecordListActivity::class.java)
//            startActivity(recordListIntent)
//        }
    }


    // 권한 부여
    private fun requestPermission(permission: String) {
        Log.d("permissionCheck", "requestPermission()")

        if (permission == "background") {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION
            )
        }

        if (permission == "fineLocation") {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            requestCode -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가된 경우 처리
                } else {
                    // 권한 거절된 경우 처리
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // 권한이 필요한 이유 설명
    private fun requestPermissionRationale(): Boolean {
        Log.d("permissionCheck", "requestPermissionRationale()")
        var result = true
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            Log.d("permissionCheck", "항상위치권한 확인")
            val builder = AlertDialog.Builder(this)
                .apply {
                    setTitle("경고")
                    setMessage("위치 권한을 항상허용하지 않았을 경우 앱이 정상작동 하지 않을 수 있습니다. \n위치 권한을 항상 허용으로 설정 하시겠습니까?")
                    setPositiveButton("네") { _, _ ->
                        // 권한 요청
                        requestPermission("background")
                    }
                    setNegativeButton("아니오") { _, _ ->
                        result = false
                    }
                    show()
                }
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // 이전에 거부한 경우 권한 필요성 설명 및 권한 요청
            val builder = AlertDialog.Builder(this)
                .apply {
                    Log.d("permissionCheck", "위치권한 거부")

                    setTitle("경고")
                    setMessage("위치 권한을 거부한 경우 앱이 정상작동 하지 않을 수 있습니다. \n위치 권한을 설정 하시겠습니까?")
                    setPositiveButton("네") { _, _ ->
                        requestPermission("fineLocation")
                    }
                    setNegativeButton("아니오") { _, _ ->
                        result = false
                    }
                    show()
                }
        }

        return result
    }

    companion object {
        public const val BACKGROUND_LOCATION = 1000
        public const val FINE_LOCATION = 2000
        var isBuddy: Boolean = false

    }

}