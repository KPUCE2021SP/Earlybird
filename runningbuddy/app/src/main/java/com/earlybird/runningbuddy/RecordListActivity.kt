package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.earlybird.runningbuddy.databinding.ActivityRecordListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecordListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRecordListBinding
    private lateinit var profileAdapter: ProfileAdapter
    private val datas = mutableListOf<ProfileData>()
    val db : FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileList.addItemDecoration(verticalItemDecorator(20))
        binding.profileList.addItemDecoration(HorizontalItemDecorator(10))

//        initRecycler()
    }
    private fun initRecycler() {
        profileAdapter = ProfileAdapter(this)
        binding.profileList.adapter = profileAdapter

        //데이터 추가하는 곳곳
        datas.apply {
//            add(ProfileData(img = R.drawable.ic_launcher_foreground, date = "mary", information = 24))
//            add(ProfileData(img = R.drawable.ic_launcher_foreground, course = "jenny", information = 26))
//            add(ProfileData(img = R.drawable.ic_launcher_foreground, course = "jhon", information = 27))
//            add(ProfileData(img = R.drawable.ic_launcher_foreground, course = "ruby", information = 21))
//            add(ProfileData(img = R.drawable.ic_launcher_foreground, course = "yuna", information = 23))

            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()   //adpater에게 값이 변경되었음을 알려줌

        }

    }

    private fun queryItem(record : String){
        db.collection("records")
            .document(Firebase.auth.currentUser?.uid ?: "No User").get()
            .addOnSuccessListener {
                binding.profileList.adapter = ProfileAdapter(this)
            }
    }
}