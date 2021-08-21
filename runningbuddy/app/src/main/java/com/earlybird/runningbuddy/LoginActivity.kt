package com.earlybird.runningbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.earlybird.runningbuddy.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //(주석은 위에서 아래로 읽어내려가세요.) 사용자가 login(로그인) 버튼을 누르면, 리스너가 반응하고
        binding.login.setOnClickListener {

            //만약 username 또는 password 둘중에 하나라도 빈칸인 채로 누른다면, 입력 해달라는 text 출력
            if (binding.username.text.toString().equals("") || binding.password.text.toString().equals("")) {
                Toast.makeText(this, "Email 과 Password 를 입력 해주세요", Toast.LENGTH_SHORT).show()

            //빈칸이 없으면, 로그인 시도(로그인 성공/실패시 행동은 doLogin 함수 안에 정의되어 있음)
            } else {
                val userEmail = binding.username.text.toString()
                val password = binding.password.text.toString()
                doLogin(userEmail, password) // 아래쪽에 doLogin 함수의 정의 참고하세요
            }
        }

        //사용자가 signUp(회원가입) 버튼을 누르면 리스너가 반응하고
        binding.signUp.setOnClickListener{

            //회원정보 입력 액티비티로 넘어감(다시 로그인 하고싶어질 수 있으므로 액티비티를 죽이지는 않음)
            startActivity(
            Intent(this, SignUpActivity::class.java)
            )
        }
    }

    //이 아래는 사용자가 login 버튼을 눌렀을때
    // username 과 password 칸이 다 채워져 있다면 실행되는 doLogin 함수의 정의
    private fun doLogin(userEmail: String, password: String) {

        //함수가 호출되면, firebase 에 인자로 받은 Email 과 Password 의 인증을 요청함
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { // it: Task<AuthResult!>

                //인증에 성공하면 다음 화면으로 넘어가면서 현재 액티비티는 finish() 함수로 종료시킴
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, AfterLoginActivity::class.java)
                    )
                    finish()

                // 인증에 실패하면 '로그인 실패' 텍스트를 출력
                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
   }