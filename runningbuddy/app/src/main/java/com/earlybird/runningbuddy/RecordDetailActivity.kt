package com.earlybird.runningbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.earlybird.runningbuddy.databinding.ActivityRecordDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class RecordDetailActivity : AppCompatActivity() {
    private lateinit var datas : ProfileData
    private lateinit var binding: ActivityRecordDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mdocument = intent.getStringExtra("document")
        //Log.d("HHH","$mdocument")

        val db = FirebaseFirestore.getInstance()
        if (mdocument != null) {
            db.collection("records")
                .document(mdocument)
                .get()
                .addOnSuccessListener {document->
                    binding.joggingDate.text = document.data?.get("Date").toString()
//                    if(document!=null) Log.d("HHH","DocumentSnapshot data: ${document.data}")
//                    else Log.d("HHH","No such document")
                }
                .addOnFailureListener {
                    Log.d("HHH","addOnFailureListener",it)
                }
        }
    }
}