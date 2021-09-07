package com.earlybird.runningbuddy

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earlybird.runningbuddy.databinding.ItemRecordListBinding

class ProfileAdapter(
    private val profileList: ArrayList<ProfileData>,
    private val context: Context
) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder { //화면을 최초 로딩하여 만들어진 View가 없는 경우 레이아웃을 inflate하여 VeiwHolder를 생성
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record_list,parent,false)  //parent.context가 컨텍스트를 전달
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
        //profiledata와 뷰홀더 연결
//        val profileData : ProfileData = profileList[position]
//        holder.txtDate.text = profileData.date
//        holder.txtTime.text = profileData.time.toString()
//        holder.txtDistance.text = profileData.distance.toString()
//        holder.imgMap.image = profileData.date
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
//            val txtDate: TextView = itemView.findViewById(R.id.jogging_date)
//            val txtTime: TextView = itemView.findViewById(R.id.jogging_time)
//            val txtDistance: TextView = itemView.findViewById(R.id.jogging_distance)

            binding.root.setOnClickListener {
                Log.d("HHH", "click")
                val intent = Intent(context, RecordDetailActivity::class.java)
                intent.putExtra("document", record.document)
                startActivity(context,intent,null)

            }
        }
        //        val imgMap: ImageView = itemView.findViewById(R.id.img_map)

//        fun bind(item: ProfileData) {
//            //TextView에 데이터 세팅
//            txtDate.text = item.date
//            txtTime.text = item.time.toString()
//            txtDistance.text = item.distance.toString()
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