package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earlybird.runningbuddy.databinding.ActivityRecordDetailBinding

class RecordDetailActivity : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var profileRecyclerView : RecyclerView
    private lateinit var profileArrayList: ArrayList<ProfileData>
    private lateinit var binding: ActivityRecordDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileRecyclerView = findViewById(R.id.profileList)
        profileRecyclerView.layoutManager = LinearLayoutManager(this)
        profileRecyclerView.setHasFixedSize(true)
//        datas = intent.getParcelableExtra<Parcelable>("data") as ProfileData

        profileArrayList = arrayListOf<ProfileData>()
        getProfileData()

        Glide.with(this).load(datas.img).into(binding.imgProfile)
        binding.joggingDate.text = datas.date
    }

    private fun getProfileData() {
        dbref = FirebaseDatabase.getInstance().
    }
}