package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.bumptech.glide.Glide
import com.earlybird.runningbuddy.databinding.ActivityRecordDetailBinding

class RecordDetailActivity : AppCompatActivity() {
    private lateinit var datas : ProfileData
    private lateinit var binding: ActivityRecordDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        datas = intent.getParcelableExtra<Parcelable>("data") as ProfileData

        Glide.with(this).load(datas.img).into(binding.imgProfile)
        binding.joggingDate.text = datas.date
    }
}