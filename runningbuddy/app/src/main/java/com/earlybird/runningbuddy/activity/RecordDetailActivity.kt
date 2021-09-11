package com.earlybird.runningbuddy.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentTransaction
import com.earlybird.runningbuddy.MapFragment
import com.earlybird.runningbuddy.ProfileData
import com.earlybird.runningbuddy.R
import com.earlybird.runningbuddy.databinding.ActivityRecordDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import org.json.JSONArray
import org.json.JSONException
import kotlin.math.roundToInt

class RecordDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordDetailBinding

    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap
    private val pathList = ArrayList<LatLng>()  // 경로 저장하기 위한 리스트
    private lateinit var pathListString: String

    private lateinit var transaction: FragmentTransaction

    private var date = ""
    private var distance = 0.0
    private var time = 0.0
    private var pace = 0.0
    private var speed = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ab : ActionBar? = supportActionBar
        ab?.setTitle("세부정보")
        val mdocument = intent.getStringExtra("document")

        val db = FirebaseFirestore.getInstance()

        if (mdocument != null) {
            db.collection("records")
                .document(mdocument)
                .get()
                .addOnSuccessListener { document ->
                    date = document.data?.get("Date").toString()
                    distance = document.data?.get("Distance").toString().toDouble()
                    time = document.data?.get("Time").toString().toDouble()
                    pace = document.data?.get("averagePace").toString().toDouble()
                    speed = document.data?.get("averageSpeed").toString().toDouble()

                    pathListString = document.data?.get("PathList").toString()

                    stringToArrayList()
                    dataFormat()

                }
                .addOnFailureListener {
                    Log.d("HHH", "addOnFailureListener", it)
                }
        }

        binding.homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()

    }

    private fun dataFormat() {
        date = date.substring(0, 10)
        binding.joggingDate.text = date
        binding.joggingDistance.text = "%.1f km".format(distance)
        binding.joggingTime.text = getTimeStringFromDouble(time)
        binding.joggingSpeed.text = "%.1f km/h".format(speed)
        binding.joggingPace.text = "km당 %.1f 초".format(pace)
    }

    private fun getTimeStringFromDouble(time: Double): String { //시간을 스트링으로 변환
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = //문자 합치기
        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    private fun stringToArrayList() {
        try {
            val jsonArray = JSONArray(pathListString)
            val size = pathListString.length - 1
            Log.d("HHHAAAA", "pathListString : $pathListString")
            for (i in 0..size) {
                val jObject = jsonArray.getJSONObject(i)
                val latitude = jObject.getString("latitude").toDouble()
                val longitude = jObject.getString("longitude").toDouble()

                Log.d("HHHAAAA", "json $i, latitude : $latitude, longitude : $longitude")

                pathList.add(LatLng(latitude, longitude))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        transaction = supportFragmentManager.beginTransaction()
            .add(R.id.map, MapFragment(recordDetailActivity = this))
        transaction.commit()
    }

    fun setMap(mnaverMap: NaverMap, mpath: PathOverlay) {
        this.naverMap = mnaverMap
        this.path = mpath
        Log.d("HHHAAA", "setMap")
        path.map = null
        drawPath()

        val cameraUpdate = CameraUpdate.scrollTo(pathList[0])


        naverMap.moveCamera(cameraUpdate)
        naverMap.locationTrackingMode = LocationTrackingMode.None

    }

    private fun drawPath() {
        if (pathList.size < 2) {
            Log.d("HHHAAA", "firebasePathList.size < 2")
            Log.d("HHHAAA", "path.coords : ${pathList}")
            return
        }
        path.coords = pathList
        Log.d("HHHAAA", "path.coords : ${path.coords}")
        path.map = naverMap
    }


}