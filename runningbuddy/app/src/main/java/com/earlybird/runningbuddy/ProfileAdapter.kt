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
            binding.joggingDate.text = record.date
            binding.joggingDistance.text = record.distance
            binding.joggingTime.text = record.time

            binding.root.setOnClickListener {
                if(MainActivity.isBuddy == true) {
                    Log.d("isBuddy","Profile : isBuddy = ${MainActivity.isBuddy}")
                    val intent = Intent(context, RunningActivity::class.java)
//                    val serviceIntent = Intent(context, RunningService::class.java)
//                    serviceIntent.putExtra("savedTimePerDistance",record.timePerDistance as Serializable)
//                    Log.d("isBuddy","ProfileAdapter : timePerDistance = ${record.timePerDistance as Serializable}")
                    savedTimePerDistance = record.timePerDistance as MutableList<Double>
                    savedDistance = record.distance.toDouble()
                    Log.d("isBuddy","Profile : savedTimePerDistance = ${savedTimePerDistance}")
                    startActivity(context,intent,null)
                }else{
                    Log.d("HHH", "click")
                    val intent = Intent(context, RecordDetailActivity::class.java)
                    intent.putExtra("document", record.document)
                    startActivity(context, intent, null)
                }
            }
        }

    }
}