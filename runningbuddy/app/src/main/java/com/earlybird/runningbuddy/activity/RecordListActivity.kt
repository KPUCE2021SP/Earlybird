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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase


class RecordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordListBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var mainIntent : Intent

    val user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ab : ActionBar? = supportActionBar
        ab?.setTitle("내 기록")
        EventChangeListener()
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
                    //레이아웃 연결
                    binding.recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerView.setHasFixedSize(true)
                    binding.recyclerView.adapter = ProfileAdapter(profileArrayList,this)
                }
        }
    }
}