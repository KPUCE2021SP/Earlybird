package com.earlybird.runningbuddy

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earlybird.runningbuddy.databinding.ItemRecordListBinding

class ProfileAdapter (private val profileData: ArrayList<ProfileData>) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    var datas = mutableListOf<ProfileData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //화면을 최초 로딩하여 만들어진 View가 없는 경우 레이아웃을 inflate하여 VeiwHolder를 생성
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_record_list,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = profileData.size   //아이템 갯수를 리턴처리

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {     //layout의 view와 데이터를 연결
        val currentItem = profileData[position]

        holder.txtDate.text = currentItem.date
        holder.txtTime.text = currentItem.time
        holder.txtDistance.text = currentItem.distance
    }

    //View Holder : 각각의 뷰를 보관하는 Holder객채
    //Item 뷰들을 재활용하기 위해 각 요소를 저장해두고 사용
    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val txtDate: TextView = itemView.findViewById(R.id.jogging_date)
        val txtTime: TextView = itemView.findViewById(R.id.jogging_time)
        val txtDistance: TextView = itemview.findViewById(R.id.jogging_distance)
        val imgProfile: ImageView = itemView.findViewById(R.id.img_map)

//        fun bind(item: ProfileData) {
//            //TextView에 데이터 세팅
//            txtDate.text = item.date
//            txtTime.text = item.information.toString()
//            Glide.with(itemView).load(item.img).into(imgProfile)
//
//            //클릭 시 RecordDeatailActivity
//            itemView.setOnClickListener {
//                Intent(context,RecordDetailActivity::class.java).apply {
//                    putExtra("data",item)
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }.run{context.startActivity(this)}
//            }
//        }
    }
}