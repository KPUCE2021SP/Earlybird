package com.earlybird.runningbuddy

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProfileAdapter (private val context: Context) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    var datas = mutableListOf<ProfileData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //화면을 최초 로딩하여 만들어진 View가 없는 경우 레이아웃을 inflate하여 VeiwHolder를 생성
        val view = LayoutInflater.from(context).inflate(R.layout.item_record_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size   //아이템 갯수를 리턴처리

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {     //layout의 view와 데이터를 연결
        holder.bind(datas[position])
    }

    //View Holder : 각각의 뷰를 보관하는 Holder객채
    //Item 뷰들을 재활용하기 위해 각 요소를 저장해두고 사용
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtDate: TextView = itemView.findViewById(R.id.jogging_date)
        private val txtTime: TextView = itemView.findViewById(R.id.jogging_time)
        private val txtDistance: TextView = itemView.findViewById(R.id.jogging_distance)
        private val imgProfile: ImageView = itemView.findViewById(R.id.img_map)

        fun bind(item: ProfileData) {
            //TextView에 데이터 세팅
            txtDate.text = item.date
            txtTime.text = item.time.toString()
            txtDistance.text = item.distance.toString()
            Glide.with(itemView).load(item.img).into(imgProfile)

            //클릭 시 RecordDeatailActivity
            itemView.setOnClickListener {
                Intent(context,RecordDetailActivity::class.java).apply {
                    putExtra("data",item)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run{context.startActivity(this)}
            }
        }
    }
}