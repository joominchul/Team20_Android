package com.example.potatoservice.ui.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.potatoservice.R
import com.example.potatoservice.SplashActivity
import com.example.potatoservice.databinding.ActivitySignupInfoBinding
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.SendSignUpUserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupInfoBinding
    private var selectedAgeGroup: String? = null
    private var selectedExperience: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.completeButton.setOnClickListener { sendSignUpUserInfo() }
    }

    private fun sendSignUpUserInfo() {
        /* 회원 가입 기입 사항
        * 나이 범위 -> 청소년, 대학생, 성인
        * 봉사 경험 -> 초심자, 중급자, 상급자
        */
        val nickName = binding.nicknameEditText.text.toString()

        // 나이 그룹 설정
        val ageGroup = binding.ageRadioGroup.checkedRadioButtonId.let { selectedId ->
            when (selectedId) {
                R.id.middleSchoolButton, R.id.highSchoolButton -> "청소년"
                R.id.universityButton -> "대학생"
                R.id.adultButton -> "성인"
                else -> null
            }
        }

        //봉사 경험 그룹 설정
        val experience = binding.experienceRadioGroup.checkedRadioButtonId.let { selectedId ->
            when (selectedId) {
                R.id.firstTimeButton -> "초심자"
                R.id.someExperienceButton -> "중급자"
                R.id.lotsExperienceButton -> "상급자"
                else -> null
            }
        }


        /* 회원 가입 정보 전송
        * 빈 칸이 있으면 안 됨
        * 전송 시 헤더에 SharedPreferences 에서 jwt 꺼내와서 같이 보냄
        * 보낼 때 스프링 서버와의 json 객체 바디의 이름이 같아야 함
         */
        if (nickName.isNotEmpty() && !ageGroup.isNullOrEmpty() && !experience.isNullOrEmpty()) {
            val userInfo = SendSignUpUserInfo(nickName, ageGroup, experience)
            val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("jwt_token", null)

            Log.d("testt", "JWT Token: Bearer $jwtToken")
            Log.d(
                "testt",
                "UserInfo: nickname=$nickName, ageRange=$ageGroup, experienced=$experience"
            )

            RetrofitClient.apiService().sendUserInfo("Bearer $jwtToken", userInfo)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        when (response.code()) {
                            201 -> {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "회원가입을 환영합니다, $nickName 님!",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(this@SignUpActivity, SplashActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            400 -> {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "존재하지 않는 유저입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            401 -> {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "유효하지 않은 인증입니다. 다시 로그인 해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            409 -> {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "이미 존재하는 닉네임입니다. 다른 닉네임을 사용해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                val errorBody = response.errorBody()?.string()
                                Log.d(
                                    "testt",
                                    "Failure Response: ${errorBody ?: "No error message"}"
                                )
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "회원가입에 실패했습니다. 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("testt", "Request Failed: ${t.message}")
                        Toast.makeText(
                            this@SignUpActivity,
                            "서버 오류: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
