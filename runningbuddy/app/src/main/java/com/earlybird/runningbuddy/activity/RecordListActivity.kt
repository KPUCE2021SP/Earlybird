package com.earlybird.runningbuddy.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.earlybird.runningbuddy.ProfileAdapter
import com.earlybird.runningbuddy.ProfileData
import com.earlybird.runningbuddy.databinding.ActivityRecordListBinding
import com.github.mikephil.charting.charts.PieChart
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase


class RecordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordListBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var mainIntent : Intent
    private lateinit var tempIntent: Intent

    val user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ab : ActionBar? = supportActionBar
        ab?.setTitle("내 기록")
        EventChangeListener()
        binding.tempbtn.setOnClickListener{
            tempIntent = Intent(applicationContext, RecaordChartActivity::class.java)
            Log.d("List","인텐트 확인")
            startActivity(tempIntent)
        }
    }


    private fun EventChangeListener() {     //DB데이터보여주기
        var mdocument: String? = null
        //데이터가져오기
        var profileArrayList = arrayListOf<ProfileData>()
        if (user != null) {
            db = FirebaseFirestore.getInstance()
            db.collection("records")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.data?.get("UserID").toString().equals(user.uid)) {
                            Log.d("HHHHA", "${document.id}")
                            mdocument = document.id
                            //Log.d("HHHHA","${document}")
                            var date = document.data?.get("Date").toString()
                            var time = document.data?.get("Time").toString()
                            var distance = document.data?.get("Distance").toString()
                            var timePerDistance = document.data?.get("timePerDistance")
                            profileArrayList.add(
                                ProfileData(date, time, distance, mdocument,
                                timePerDistance as MutableList<Double>)
                            )
//                            Log.d("isBuddy","RecordListActivity : timePerDistance = ${timePerDistance as MutableList<Double>}")
                        }
                    }

                    Log.d("profile","profileSzie : ${profileArrayList.size}")
                    Log.d("profile","profileArraryLIst[0] : ${profileArrayList[0].date}")
                    for (j in 1..(profileArrayList.size - 1)) {

                        var key = profileArrayList[j]  //profileArrayList[j]를 정렬된 배열 profileArrayList[1..j-1]에 삽입한다.
                        var i = j-1

                        while(i>=0 && profileArrayList[i].date > key.date) {

                            profileArrayList[i+1] = profileArrayList[i]
                            i = i-1
                        }
                        profileArrayList[i+1] = key

                    }
                    //레이아웃 연결
                    binding.recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerView.setHasFixedSize(true)
                    binding.recyclerView.adapter = ProfileAdapter(profileArrayList,this)
                }
        }
    }
}