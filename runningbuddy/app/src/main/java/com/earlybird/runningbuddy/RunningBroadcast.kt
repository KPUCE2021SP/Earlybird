//package com.earlybird.runningbuddy
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Bundle
//import android.util.Log
//
//class RunningBroadcast : BroadcastReceiver() {
//    private val br = RunningBroadcast()
//    private val intentFilter = IntentFilter()
//
//    fun onCreate(savedInstanceState: Bundle?){
//        val filter = IntentFilter(Intent.ACTION_LOCALE_CHANGED)
//        onReceive(br, filter)
//
//    }
//
//
//    override fun onReceive(context: Context, intent: Intent) {
//        fun onReceiver(context: Context?, intent: Intent?){
//            Log.d("브로드캐스트 슈루룽","Intent: $intent")
//        }
//
//    }
//}