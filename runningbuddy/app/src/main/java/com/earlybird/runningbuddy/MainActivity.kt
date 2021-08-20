package com.earlybird.runningbuddy

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import com.earlybird.runningbuddy.databinding.ActivityMainBinding

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

        binding.runButton.setOnClickListener { //runButton클릭 시
            startActivity(activityIntent)
        }
    }
}