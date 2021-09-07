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
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay

class RecordDetailActivity : AppCompatActivity() {
    private lateinit var datas: ProfileData
    private lateinit var binding: ActivityRecordDetailBinding

    private lateinit var path: PathOverlay
    private lateinit var naverMap: NaverMap

    private var firebasePathList = ArrayList<LatLng>() //경로
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
                    Log.d("HHHAAAA", "PathList : ${document.data?.get("PathList")}")

                    val splitArray = pathListString.split("}")
                    Log.d("HHHAAAA","0 : ${splitArray[0]},1 : ${splitArray[1]}")

//                    if(document!=null) Log.d("HHH","DocumentSnapshot data: ${document.data}")
//                    else Log.d("HHH","No such document")
                }
                .addOnFailureListener {
                    Log.d("HHH", "addOnFailureListener", it)
                }
        }

        transaction = supportFragmentManager.beginTransaction()
            .add(R.id.map, MapFragment(recordDetailActivity = this))
        transaction.commit()
    }

    private fun StringToArrayList(){

    }

    fun setMap(naverMap: NaverMap, path: PathOverlay) {
//        this.naverMap = naverMap
//        this.path = path
        Log.d("HHHAAA", "setMap")
        path.map = null

        if (firebasePathList.size < 2) {
            Log.d("HHHAAA", "firebasePathList.size < 2")
            Log.d("HHHAAA", "path.coords : ${firebasePathList}")
            return
        }
        path.coords = firebasePathList
        Log.d("HHHAAA", "path.coords : ${path.coords}")
        path.map = naverMap

    }
}