package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import com.earlybird.runningbuddy.databinding.ActivityRecordDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import org.json.JSONArray
import org.json.JSONException

class RecordDetailActivity : AppCompatActivity() {
    private lateinit var datas: ProfileData
    private lateinit var binding: ActivityRecordDetailBinding

    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap
    private val pathList = ArrayList<LatLng>()  // 경로 저장하기 위한 리스트

    //private var firebasePathList = ArrayList<LatLng>() //경로
    private lateinit var pathListString: String

    //private val firebasePathList: ArrayList<LatLng>()
    private lateinit var transaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mdocument = intent.getStringExtra("document")
        //Log.d("HHH","$mdocument")

        val db = FirebaseFirestore.getInstance()
        if (mdocument != null) {
            db.collection("records")
                .document(mdocument)
                .get()
                .addOnSuccessListener { document ->
                    binding.joggingDate.text = document.data?.get("Date").toString()
                    binding.joggingDistance.text = document.data?.get("Distance").toString()
                    binding.joggingTime.text = document.data?.get("Time").toString()

                    pathListString = document.data?.get("PathList").toString()
//                    Log.d("HHHAAAA", "PathList : ${document.data?.get("PathList")}")
//                    val path = pathListString.substring(1,pathListString.length-1)
//                    Log.d("HHHAAAA", "PathList : ${path}")
//                    val splitArray = pathListString.split("}")
//                    Log.d("HHHAAAA","0 : ${splitArray[0]},1 : ${splitArray[1]}")
                    StringToArrayList()
//                    if(document!=null) Log.d("HHH","DocumentSnapshot data: ${document.data}")
//                    else Log.d("HHH","No such document")
                }
                .addOnFailureListener {
                    Log.d("HHH", "addOnFailureListener", it)
                }
        }


    }

    private fun StringToArrayList() {
        try {
            val jsonArray = JSONArray(pathListString)
            val size = pathListString.length -1
            Log.d("HHHAAAA","pathListString : $pathListString")
            for (i in 0..size) {
                val jObject = jsonArray.getJSONObject(i)
                val latitude = jObject.getString("latitude").toDouble()
                val longitude = jObject.getString("longitude").toDouble()

                Log.d("HHHAAAA","json $i, latitude : $latitude, longitude : $longitude")

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

    private fun drawPath(){
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