package com.siyeon.haniumproject

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.siyeon.haniumproject.databinding.ActivityRegisterBinding // View Binding import 추가

class Register : AppCompatActivity() {
    private val TAG: String = "Register"
    private lateinit var binding: ActivityRegisterBinding // View Binding 변수 추가
    private var isExistBlank = false
    private var isPWSame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater) // View Binding 초기화
        setContentView(binding.root) // View Binding의 root 뷰를 설정

        binding.registerRegister.setOnClickListener {
            Log.d(TAG, "회원가입 버튼 클릭")

            // editText로부터 유저가 입력한 값들을 받아온다
            val registerid = binding.registerId.text.toString()
            val registerpw = binding.registerPw.text.toString()
            val pwRe = binding.registerPwRe.text.toString()

            // 입력 상자 초기화
            binding.registerId.text.clear()
            binding.registerPw.text.clear()
            binding.registerPwRe.text.clear()

            // 비밀번호와 비밀번호 확인이 다른 경우, isPWSame 초기화
            if (registerpw != pwRe) {
                isPWSame = false
            }

            // 유저가 항목을 다 채우지 않았을 경우
            if (registerid.isEmpty() || registerpw.isEmpty() || pwRe.isEmpty()) {
                isExistBlank = true
            } else {
                if (registerpw == pwRe) {
                    isPWSame = true
                }
            }

            if (!isExistBlank && isPWSame) {

                // 유저가 입력한 id, pw를 쉐어드에 저장한다.
                val sharedPreference = getSharedPreferences("file name", Context.MODE_PRIVATE)
                val editor = sharedPreference.edit()
                editor.putString("id", registerid)
                editor.putString("pw", registerpw)
                editor.apply()

                // 회원가입 성공 토스트 메세지 띄우기
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()

                // 로그인 화면으로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료. 이번에 추가함.

            } else {

                // 상태에 따라 다른 다이얼로그 띄워주기
                if (isExistBlank) {   // 작성 안한 항목이 있을 경우
                    dialog("blank")
                } else if (!isPWSame) { // 입력한 비밀번호가 다를 경우
                    dialog("not same")
                }
            }
        }

    }

    // 회원가입 실패시 다이얼로그를 띄워주는 메소드
    fun dialog(type: String){
        val dialog = AlertDialog.Builder(this)

        // 작성 안한 항목이 있을 경우
        if(type.equals("blank")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("입력란을 모두 작성해주세요")
        }
        // 입력한 비밀번호가 다를 경우
        else if(type.equals("not same")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("비밀번호가 다릅니다")
        }

        val dialog_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when(which){
                    DialogInterface.BUTTON_POSITIVE ->
                        Log.d(TAG, "다이얼로그")
                }
            }
        }

        dialog.setPositiveButton("확인",dialog_listener)
        dialog.show()
    }
}