package com.earlybird.runningbuddy

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earlybird.runningbuddy.databinding.ActivityRecordListBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RecordListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRecordListBinding

    private lateinit var db : FirebaseFirestore
    val user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EventChangeListener()
    }

    private fun EventChangeListener() {     //DB데이터보여주기
        //데이터가져오기
        var profileArrayList = arrayListOf<ProfileData>()
        if (user != null) {
            db = FirebaseFirestore.getInstance()
            db.collection("records")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.data?.get("UserID").toString().equals(user.uid)) {
                            var date = document.data?.get("Date").toString()
                            var time = document.data?.get("Time").toString()
                            var distance = document.data?.get("Distance").toString()
                            profileArrayList.add(ProfileData(date, time, distance))
                        }
                    }
                    //레이아웃 연결
                    binding.recyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerView.setHasFixedSize(true)
                    binding.recyclerView.adapter = ProfileAdapter(profileArrayList)
                }
        }
    }
}