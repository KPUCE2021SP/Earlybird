package com.earlybird.runningbuddy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding

class RunningActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRunningBinding

    private val br = MyBroadcastReceiver()
    private val intentFilter = IntentFilter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.stopButton.setOnClickListener { //stopButton클릭 시
            val intent = Intent(this,DataViewActivity::class.java)  //DataViewActivity로 넘어감
            startActivity(intent)
        }

        // broadcast
        Log.d("service22","broadcast")
        intentFilter.addAction("DistanceService")
        registerReceiver(br,intentFilter)

        // RunningService
        val intent = Intent(this, RunningService::class.java)
        startService(intent)
        Log.d("serviceCycle","startService")


        // MapFragment
        val transaction = supportFragmentManager.beginTransaction().add(R.id.map,MapFragment(true))
        transaction.commit()
        Log.d("Map22","runningActivity")

    }
}

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("service22", "[MyBroadcastReceiver] Intent: $intent")
        if(intent!=null){
            when(intent.action){
                "DistanceService"->{
                    Log.d("service22","[MyBroadcastReceiver] DistanceService")
                }
            }
        }

    }
}