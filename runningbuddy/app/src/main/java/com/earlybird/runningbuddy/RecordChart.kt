package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.os.SystemClock
import com.earlybird.runningbuddy.databinding.ActivityMainBinding
import com.earlybird.runningbuddy.databinding.ActivityRecordChartBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

public class RecordChart : AppCompatActivity() {

    var isrunning = false
    private lateinit var binding: ActivityRecordChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_chart)
        binding = ActivityRecordChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            if (isrunning == false) {
                isrunning = true
                binding.startButton.text = "그래프 구현중"
                binding.startButton.isClickable = false
                val thread = ThreadClass()
                thread.start()
            }
        }

    }


    inner class ThreadClass : Thread() {

        override fun run() { //그래프 초기설정
            val input = Array<Double>(100,{Math.random()})
            // Entry 배열 생성
            var entries: ArrayList<DropBoxManager.Entry> = ArrayList()
            // Entry 배열 초기값 입력
            entries.add(DropBoxManager.Entry(0F, 0F))
            // 그래프 구현을 위한 LineDataSet 생성
            var dataset: LineDataSet = LineDataSet(entries, "input")
            // 그래프 data 생성 -> 최종 입력 데이터
            var data: LineData = LineData(dataset)
            // chart.xml에 배치된 lineChart에 데이터 연결
            binding.lineChart.data = data

            runOnUiThread {
                // 그래프 생성
                binding.lineChart.animateXY(1, 1)
            }

            for (i in 0 until input.size){//그래프 표현식

                SystemClock.sleep(10)
                data.addEntry(Entry(i.toFloat(), input[i].toFloat()), 0)
                data.notifyDataChanged()
                binding.lineChart.notifyDataSetChanged()
                binding.lineChart.invalidate()
            }
            binding.startButton.text = "난수 생성 시작"
            binding.startButton.isClickable = true

            isrunning = false

        }
    }
}