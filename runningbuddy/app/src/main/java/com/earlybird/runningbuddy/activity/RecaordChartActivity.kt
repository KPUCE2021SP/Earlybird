package com.earlybird.runningbuddy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import android.graphics.Color
import android.view.View
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.R
import android.util.Log
import com.earlybird.runningbuddy.databinding.ActivityRecaordChartBinding
import com.earlybird.runningbuddy.databinding.ActivityRecordListBinding
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry


class RecaordChartActivity : AppCompatActivity() {

    companion object {
        val entries = arrayListOf<Entry>()
    }

    private var lineChart: LineChart? = null
    private lateinit var binding: ActivityRecaordChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(com.earlybird.runningbuddy.R.layout.activity_recaord_chart)
        //lineChart = findViewById<View>(R.id.chart) as LineChart
        binding = ActivityRecaordChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lineChart = binding.chart
      //  val entries: MutableList<Map.Entry<*, *>> = ArrayList()
        Log.d("Rec","데이터 전")

        Log.d("Rec","데이터 후")
       /* entries.add(MutableMap.MutableEntry<Any?, Any?>(1, 1))
        entries.add(MutableMap.MutableEntry<Any?, Any?>(2, 2))
        entries.add(MutableMap.MutableEntry<Any?, Any?>(3, 0))
        entries.add(MutableMap.MutableEntry<Any?, Any?>(4, 4))
        entries.add(MutableMap.MutableEntry<Any?, Any?>(5, 3))*/

        val lineDataSet = LineDataSet(entries, "달린거리")
        lineDataSet.lineWidth = 1f
        lineDataSet.circleRadius = 6f
        lineDataSet.setCircleColor(Color.parseColor("#FFFF5F00"))
        lineDataSet.setCircleColorHole(Color.TRANSPARENT)
        lineDataSet.color = Color.parseColor("#FFFF5F00")
        lineDataSet.setDrawCircleHole(true)
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawHighlightIndicators(false)
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        lineChart.setData(lineData)

        val xAxis = lineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.getAxisLeft()
        yLAxis.textColor = Color.BLACK

        val yRAxis = lineChart.getAxisRight()
        yRAxis.setDrawLabels(false)
        yRAxis.setDrawAxisLine(false)
        yRAxis.setDrawGridLines(false)

        val description = Description()
        description.text = ""

        lineChart.setDoubleTapToZoomEnabled(false)
        lineChart.setDrawGridBackground(false)
        lineChart.setDescription(description)
        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic)
        lineChart.invalidate()    }
}