package com.earlybird.runningbuddy.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.earlybird.runningbuddy.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ab: ActionBar? = supportActionBar
        ab?.setTitle("회원가입")
        //(주석은 위에서 아래로 읽여내려 가세요.) db 변수에 firebaseDB를 쓰기위한 객체를 할당함
        val db: FirebaseFirestore = Firebase.firestore

        //성별을 저장하기 위한 'sex' 변수 선언, 사용자의 남/여 버튼 선택에 따라 sex 변수의 값과 사용자의 현재선택 성별을 보여주는 'sexView' 의 내용이 변경됨
        var Sex: String = ""
        binding.MaleButton.setOnClickListener {
            binding.sexView.text = "남자"
            Sex = "male"
        }
        binding.FemaleButton.setOnClickListener {
            binding.sexView.text = "여자"
            Sex = "female"
        }

        //사용자가 회원가입을 위한 정보를 모두 입력 후 맨아래 버튼인 EndButton 을 누르면 리스너가 동작함
        binding.EndButton.setOnClickListener {

            //리스너 안에서 if 문을 이용해 가입정보중 한칸이라도 빈곳이 있는지 검사하고
            if (binding.IDtext.text.toString().equals("")
                || binding.PWtext.text.toString().equals("")
                || binding.NameText.text.toString().equals("")
                || binding.HeightText.text.toString().equals("")
                || binding.WeightText.text.toString().equals("")
                || binding.PWChecktext.text.toString().equals("")
                || Sex.equals("")
            ) {

                //빈칸이 있다면 빈칸을 채워달라는 토스트 메세지를 출력함
                Toast.makeText(this, "빈칸을 모두 채워주세요", Toast.LENGTH_SHORT).show()

            } //빈칸은 없지만 만약 비밀번호 확인하기 와 비밀번호가 일치하지 않는다면 다시 입력 해달라고 토스트 메세지 출력함
            else if (!binding.PWChecktext.text.toString().equals(binding.PWtext.text.toString())) {
                Toast.makeText(this, "비밀번호가 다릅니다.\n 다시 확인 해주세요.", Toast.LENGTH_SHORT).show()
            } else {

                //사용자가 위의 문제들 없이 다 채워넣고 EndButton을 눌렀다면, firebase에 아이디와 비밀번호를 전달하며 회원가입을 시도함
                Firebase.auth.createUserWithEmailAndPassword(
                    binding.IDtext.text.toString(),
                    binding.PWtext.text.toString()

                    //정상적인 아이디와 비밀번호를 입력했다면 성공했을경우의 리스너가 동작함
                ).addOnCompleteListener(this) { // it: Task<AuthResult!>

                    if (it.isSuccessful) {

                        //그리고 아이디와 패스워드를 제외한 회원 정보를 DB에 넣기위한 hashMap을 생성함
                        val infoMap = hashMapOf(
                            "Name" to binding.NameText.text.toString(),
                            "Height" to binding.HeightText.text.toString().toDouble(),
                            "Weight" to binding.WeightText.text.toString().toDouble(),
                            "Sex" to Sex
                        )


                        //그리고 미리 준비된 DB 콜렉션인 'users' 에 방금 만들어진 유저의 고윳값인 UID(복잡한 문자 및 숫자로 이루어짐) 를 제목으로하는
                        //문서(document)를 생성하고, 바로 위에서 만든 hashMap 인 'infoMap' 을 문서의 내용으로 채워넣음
                        db.collection("users")
                            .document(Firebase.auth.currentUser?.uid ?: "No User")
                            .set(infoMap)

                        //이제  LoginActivity 로 화면을 전환하며, SignUpActivity 는 종료함
                        startActivity(
                            Intent(this, LoginActivity::class.java)
                        )
                        finish()
                    } else {

                        //만약 정상적인 아이디나 비밀번호가 아니여서 (이미 가입된 이메일 이거나 6자리 이하의 비밀번호 등) 가입에 실패하면, 실패했다는 text를 출력함
                        Toast.makeText(this, "회원가입 실패. 개발자에게 문의하세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}