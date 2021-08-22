package com.earlybird.runningbuddy

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.earlybird.runningbuddy.databinding.ActivityDataviewBinding
import com.earlybird.runningbuddy.databinding.ActivityRunningBinding

class DataViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataviewBinding
    private lateinit var recordListIntent : Intent  //Intent for recordListActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataviewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recordListIntent = Intent(applicationContext,RecordListActivity::class.java)
        binding.recordButton.setOnClickListener {
            startActivity(recordListIntent)
        }

    }

    override fun onBackPressed() {
        return
    }
}