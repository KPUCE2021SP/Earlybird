package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earlybird.runningbuddy.databinding.ActivityRecordListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecordListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileArrayList: ArrayList<ProfileData>
    private lateinit var binding : ActivityRecordListBinding
    private lateinit var profileAdapter: ProfileAdapter
//    private val datas = mutableListOf<ProfileData>()
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.recyclerView.addItemDecoration(verticalItemDecorator(20))
//        binding.recyclerView.addItemDecoration(HorizontalItemDecorator(10))

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        profileAdapter = ProfileAdapter(profileArrayList)

        recyclerView.adapter = profileAdapter

        EventChangeListener()
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("records").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error",error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        profileArrayList.add(dc.document.toObject(ProfileData::class.java))
                    }
                }
                profileAdapter.notifyDataSetChanged()
            }

        })
    }
//    private fun initRecycler() {
//        profileAdapter = ProfileAdapter(this)
//        binding.profileList.adapter = profileAdapter
//
//        //데이터 추가하는 곳곳
//        datas.apply {
//
////            add(ProfileData(img = R.drawable.ic_launcher_foreground, date = "mary", time = 24))
////            add(ProfileData(img = R.drawable.ic_launcher_foreground, date = "jenny", information = 26))
////            add(ProfileData(img = R.drawable.ic_launcher_foreground, date = "jhon", information = 27))
////            add(ProfileData(img = R.drawable.ic_launcher_foreground, course = "ruby", information = 21))
////            add(ProfileData(img = R.drawable.ic_launcher_foreground, course = "yuna", information = 23))
//
//            profileAdapter.datas = datas
//            profileAdapter.notifyDataSetChanged()   //adpater에게 값이 변경되었음을 알려줌
//
//        }
//
//    }
//
//    private fun queryItem(record : String){
//        db.collection("records")
//            .document(Firebase.auth.currentUser?.uid ?: "No User").get()
//            .addOnSuccessListener {
//                binding.profileList.adapter = ProfileAdapter(this)
//            }
//    }
}