package com.example.potatoservice.ui.sign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.KakaoSdk
import com.example.potatoservice.MainActivity
import com.example.potatoservice.MainViewModel
import com.example.potatoservice.R
import com.example.potatoservice.databinding.ActivitySignInBinding
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.LoginRequest
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val mainViewModel: MainViewModel by viewModels() // ViewModel 주입

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        KakaoSdk.init(this, getString(R.string.kakao_api_key))
        tryLoginKakao()
    }

    /* 카카오 서버 인가 코드 요청
    * 로그인 버튼 클릭시, 카카오 앱을 통해 로그인하고 인가 코드를 받아들임
    * 받아들인 인가 코드를 스프링 서버로 보냄으로써 jwt와 avatar(userInfo) 받아옴
     */
    private fun tryLoginKakao() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("testt", "Login failed: ${error.message}")
            } else if (token != null) {
                Log.d("testt", "Login successful, token: ${token.accessToken}")
                sendAccessTokenToServer(token.accessToken)
            }
        }
        UserApiClient.instance.run {
            if (isKakaoTalkLoginAvailable(this@SignInActivity)) {
                loginWithKakaoTalk(this@SignInActivity, callback = callback)
            } else {
                loginWithKakaoAccount(this@SignInActivity, callback = callback)
            }
        }
    }

    /* 스프링 서버에 jwt 및 avatar 정보 요청
    * userInfo 가 null 값이면 회원 가입 activity / userInfo 가 있으면 메인 Activity 이동
    * 이동할 때 받아들인 avatar 객체 정보를 SharedPreferences에 넣습니다.
    * 필요할 때마다 jwtToken을 사용하시면 됩니다. -> 필요할 때란, retrofit으로 스프링 서버와 데이터를 주고 받을 때, 헤더에 jwtToken 변수를 넣어줘야 합니다.
     */
    private fun sendAccessTokenToServer(accessToken: String) {
        RetrofitClient.apiService().kakaoLogin(accessToken)
            .enqueue(object : Callback<LoginRequest> {
                override fun onResponse(
                    call: Call<LoginRequest>,
                    response: Response<LoginRequest>
                ) {

                    if (response.isSuccessful) {
                        val jwtToken = response.headers()["token"]
                        val avatarInfo = response.body()?.avatar
                        Log.d("testt", "JWT Token: $jwtToken")
                        Log.d("testt", "Avatar Info: $avatarInfo")
                        if (jwtToken != null) {

                            /*
                            * SharedPreferences -> MainVM 저장 방법 변경
                            * 일단 shared 방식도 냅두겠습니다. Main에서 꺼내 써주세요.
                             */
                            if(avatarInfo != null){
                                mainViewModel.setLoginData(jwtToken,avatarInfo)  //우선 avatarInfo가 null이 아니라고 확정
                            }
                            val sharedPref =
                                getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("jwt_token", jwtToken)
                                putString(
                                    "user_info",
                                    avatarInfo?.toString()
                                ) // 필요한 경우 JSON 형태로 직렬화 가능
                                apply()
                            }
                            val intent = Intent(
                                this@SignInActivity,
                                if (avatarInfo != null) MainActivity::class.java else SignUpActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("testt", "JWT token not found in headers")
                        }
                    } else {
                        Log.e("testt", "Backend login failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<LoginRequest>, t: Throwable) {
                    Log.e("testt", "Backend login error: ${t.message}")
                }
            })
    }
}