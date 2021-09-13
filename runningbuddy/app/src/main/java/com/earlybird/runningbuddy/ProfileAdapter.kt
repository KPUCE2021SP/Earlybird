package com.earlybird.runningbuddy

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.earlybird.runningbuddy.activity.MainActivity
import com.earlybird.runningbuddy.activity.RecordDetailActivity
import com.earlybird.runningbuddy.activity.RunningActivity
import com.earlybird.runningbuddy.databinding.ItemRecordListBinding
import kotlin.math.roundToInt

class ProfileAdapter(
    private val profileList: ArrayList<ProfileData>,
    private val context: Context
) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    companion object{
        var savedTimePerDistance : MutableList<Double>? = null
        var savedDistance : Double? = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder { //화면을 최초 로딩하여 만들어진 View가 없는 경우 레이아웃을 inflate하여 VeiwHolder를 생성

        Log.d("HHHVIEWHOLDER", "onCreateViewHolder()")
        return ViewHolder(
            ItemRecordListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {//아이템 갯수를 리턴처리
        return profileList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("HHHVIEWHOLDER", "onBindViewHolder()")
        holder.bind(profileList[position])
    }

    //View Holder : 각각의 뷰를 보관하는 Holder객채
    //Item 뷰들을 재활용하기 위해 각 요소를 저장해두고 사용
    inner class ViewHolder(private val binding: ItemRecordListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: ProfileData) {
            Log.d("HHHVIEWHOLDER", "bind()")
            val date=record.date.substring(0,10)
            binding.joggingDate.text = date
            binding.joggingDistance.text = "%.1f km".format((record.distance).toDouble())
            binding.joggingTime.text = getTimeStringFromDouble((record.time).toDouble())

            binding.root.setOnClickListener {
                if(MainActivity.isBuddy == true) {
                    Log.d("isBuddy","Profile : isBuddy = ${MainActivity.isBuddy}")
                    val intent = Intent(context, RunningActivity::class.java)

                    savedTimePerDistance = record.timePerDistance as MutableList<Double>
                    savedDistance = record.distance.toDouble()
                    Log.d("isBuddy","Profile : savedTimePerDistance = ${savedTimePerDistance}")
                    startActivity(context,intent,null)
                } else {
                    Log.d("HHH", "click")
                    val intent = Intent(context, RecordDetailActivity::class.java)
                    intent.putExtra("document", record.document)
                    startActivity(context, intent, null)
                }
            }
        }

        private fun getTimeStringFromDouble(time: Double): String { //시간을 스트링으로 변환
            val resultInt = time.roundToInt()
            val hours = resultInt % 86400 / 3600
            val minutes = resultInt % 86400 % 3600 / 60
            val seconds = resultInt % 86400 % 3600 % 60

            return makeTimeString(hours, minutes, seconds)
        }

        private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = //문자 합치기
            String.format("%02d:%02d:%02d", hours, minutes, seconds)


    }
}