package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.earlybird.runningbuddy.databinding.ActivityRecordListBinding

class RecordListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRecordListBinding
    private lateinit var profileAdapter: ProfileAdapter
    private val datas = mutableListOf<ProfileData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profile.addItemDecoration(verticalItemDecorator(20))
        binding.profile.addItemDecoration(HorizontalItemDecorator(10))

        initRecycler()
    }
    private fun initRecycler() {
        profileAdapter = ProfileAdapter(this)
        binding.profile.adapter = profileAdapter

        //데이터 추가하는 곳곳
        datas.apply {
            add(ProfileData(img = R.drawable.ic_launcher_foreground, date = "mary", information = 24))
            add(ProfileData(img = R.drawable.ic_launcher_foreground, date = "jenny", information = 26))

            profileAdapter.datas = datas
            profileAdapter.notifyDataSetChanged()   //adpater에게 값이 변경되었음을 알려줌

        }

    }
}