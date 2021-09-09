package com.earlybird.runningbuddy

import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent  //RunningService 를 위한 intent
    private lateinit var activityIntent: Intent    //RunningActivity 를 위한 intent
    private lateinit var recordListIntent: Intent
    private lateinit var adapterIntent: Intent

    private lateinit var transaction: FragmentTransaction



    @RequiresApi(Build.VERSION_CODES.Q)
    val permissionArray = arrayOf(
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transaction = supportFragmentManager.beginTransaction().add(R.id.map, MapFragment())
        transaction.commit()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        serviceIntent =
            Intent(applicationContext, RunningService::class.java)   //RunningService 와 intent
        activityIntent = Intent(this, RunningActivity::class.java) //RunningActivity 와 intent

        startRunning()

        binding.signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Handler().postDelayed({
                System.exit(0)
            }, 1000)
        }

        binding.userInfoButton.setOnClickListener {
            Handler().postDelayed({
                startActivity(
                    Intent(this, UserInfo::class.java)
                )
            }, 1000)
        }

        binding.buddyButton.setOnClickListener {
            isBuddy = !isBuddy
            Log.d("isBuddy","")
            recordListIntent = Intent(this, RecordListActivity::class.java)
            startActivity(recordListIntent)
        }
    }

    private fun startRunning() {
        binding.runButton.setOnClickListener { //runButton 클릭 시
            // 권한 부여
            //requestPermission()
//        // 권한 설정
            if(!requestPermissionRationale()) return@setOnClickListener

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
            } else {
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
                startActivity(activityIntent)
            }
        }
    }


    // 권한 부여
    private fun requestPermission(permission:String) {
        Log.d("permissionCheck", "requestPermission()")

        if(permission == "background"){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION
            )
        }

        if(permission == "fineLocation"){
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
        when(requestCode){
            requestCode ->{
                if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // 권한 허가된 경우 처리
                } else{
                    // 권한 거절된 경우 처리
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // 권한이 필요한 이유 설명
    private fun requestPermissionRationale() :Boolean{
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