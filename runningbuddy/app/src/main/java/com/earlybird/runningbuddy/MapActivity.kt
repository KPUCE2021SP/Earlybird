package com.earlybird.runningbuddy

import android.Manifest
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource

object MapActivity : AppCompatActivity(), OnMapReadyCallback {


    private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

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

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // getMapAsync 를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady 에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this)


    }

    // 권한 확인
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )
        ) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
                Log.d("map", "권한 거부됨")
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // NaverMap 인스턴스가 준비되면 호출되는 콜백 메서드.
    override fun onMapReady(naverMap: NaverMap) {

        this.naverMap = naverMap
        // 지도 확대 축소 설정
//        naverMap.minZoom = 18.0
//        naverMap.maxZoom = 18.0

        //지도 위치를 현 위치로 설정
        naverMap.locationSource = locationSource

        // 지도 위 이동 거리 표시
        val path = PathOverlay()
        // 좌표열 저장
        path.coords = listOf(
            LatLng(37.57152, 126.97714),
            LatLng(37.56607, 126.98268),
        )
        // 좌표열 변경을 위한 변수
        val coords = mutableListOf(
            LatLng(37.57152, 126.97714),
            LatLng(37.56607, 126.98268),
        )
        path.coords = coords

        // 움직일때마다 좌표 표시시 + 위치를 그려줄 수 있는 함수 호출
        naverMap.addOnLocationChangeListener { location ->
            Toast.makeText(
                this, "${location.latitude}, ${location.longitude}",
                Toast.LENGTH_SHORT
            ).show()
            drawMap(coords, location, path)
        }

        // 위치 추적 활성화
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    // 위치 드로우
    private fun drawMap(coords: MutableList<LatLng>, location: Location, path: PathOverlay) {
        coords[0] = LatLng(location)
        path.coords.add(LatLng(location))

        path.coords = coords

        path.map = naverMap
    }

//    private fun clear(coords: MutableList<LatLng>, location: Location, path: PathOverlay) {
//        path.map = null
//    }
    public fun getCurrentLocation(naverMap: NaverMap) {

    }
    
}