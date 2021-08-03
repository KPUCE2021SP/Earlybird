package com.earlybird.runningbuddy

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mNaverMap: NaverMap? = null
    private var mLocationSource: FusedLocationSource? = null
    private val PERMISSION_REQUEST_CODE = 100
    private val PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //네이버 지도 객체 생성
        val fm: FragmentManager = getSupportFragmentManager()
        var mapFragment: MapFragment = fm.findFragmentById(R.id.map) as MapFragment
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fm.beginTransaction().add(R.id.map, mapFragment).commit()
        }

        // 위치를 반환
        mLocationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        mapFragment.getMapAsync(this)

//        override fun onRequestPermissionsResult(
//            requestCode: Int,
//            permissions: Array<out String>,
//            grantResults: IntArray
//        ) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//            //request code와 권한획득여부 확인
//            if (requestCode == PERMISSION_REQUEST_CODE) {
//                if (grantResults.size > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                ) {
//                    mNaverMap?.setLocationTrackingMode(LocationTrackingMode.Follow)
//                }
//            }
//        }
    }

    override fun onMapReady(p0: NaverMap) {

    }
}