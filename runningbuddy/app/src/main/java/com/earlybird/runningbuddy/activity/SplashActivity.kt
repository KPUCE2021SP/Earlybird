package com.earlybird.runningbuddy.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.earlybird.runningbuddy.databinding.ActivitySplashBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val currentAndroidID =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val currentUid = Firebase.auth.currentUser?.uid ?: "No User"
        val db: FirebaseFirestore = Firebase.firestore
        var dbAndroidID: String = ""
        db.collection("logins").document(currentUid).get().addOnSuccessListener {
            dbAndroidID = it["AndroidID"].toString()
        }
        Handler().postDelayed({
            if (isOnline(this)) {
                if (isAutoLogin(currentUid, currentAndroidID, dbAndroidID)) {
                    Handler().postDelayed({
                        Toast.makeText(this, "자동 로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(this, MainActivity::class.java)
                        )
                        ActivityCompat.finishAffinity(this)
                    }, 2000)
                } else {
                    Handler().postDelayed({
                        Toast.makeText(this, "자동 로그인 실패", Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(this, LoginActivity::class.java)
                        )
                        ActivityCompat.finishAffinity(this)
                    }, 2000)
                }
            } else {
                Toast.makeText(this, "인터넷 연결을 확인해주세요\n 2초뒤에 종료됩니다.", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    System.exit(0)
                }, 2000)
            }
        }, 1000)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun isAutoLogin(currentUid: String, currentAndroidID: String, dbAndroidID: String): Boolean {
        if (currentUid == "No User") {
            return false
        }
        if (!dbAndroidID.equals(currentAndroidID)) {
            return false
        }
        return true
    }
}