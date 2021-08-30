package com.earlybird.runningbuddy

import android.content.*
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentTransaction
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent  //RunningService를 위한 intent
    private lateinit var activityIntent: Intent    //RunningActivity를 위한 intent

    private lateinit var transaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transaction = supportFragmentManager.beginTransaction().add(R.id.map,MapFragment())
        transaction.commit()


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, RunningService::class.java)   //RunningService와 intent
        activityIntent = Intent(this,RunningActivity::class.java) //RunningActivity와 intent

        startRunning()

        binding.signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Handler().postDelayed({
                System.exit(0)
            }, 1000)
        }
    }

    private fun startRunning(){
        binding.runButton.setOnClickListener { //runButton클릭 시
            //LocationManager
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            // gps 를 껏을 경우
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                val builder = AlertDialog.Builder(this)
                    .apply {
                        setTitle("경고")
                        setMessage("GPS가 꺼져있습니다. GPS를 키시겠습니까?")
                        setPositiveButton("네") { dialog, which ->
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)
                        }
                        setNegativeButton("아니오",DialogInterface.OnClickListener{ dialog, which ->
                            return@OnClickListener
                        })
                        show()
                    }
            }
            else{
                startActivity(activityIntent)
            }
        }
    }

}