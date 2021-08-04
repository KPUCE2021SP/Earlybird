package com.earlybird.runningbuddy

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
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

    //  클라이언트가 서비스와 상호작용하는 데 사용할 수 있는 프로그래밍 인터페이스를 정의하는 IBinder 객체를 반환
    override fun onBind(intent: Intent): IBinder {
        // 메서드를 항상 구현해야 하기에 일단 null 반환
        val binder:IBinder? = null
        return binder!!
    }

    //


    // 서비스가 수신하는 마지막 호출
    override fun onDestroy(){

    }
}