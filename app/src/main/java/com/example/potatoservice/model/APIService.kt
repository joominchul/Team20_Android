package com.example.potatoservice.model

import com.example.potatoservice.model.remote.ActivityDetail
import com.example.potatoservice.model.remote.ActivityResponse
import com.example.potatoservice.model.remote.AddressResponse
import com.example.potatoservice.model.remote.LoginRequest
import com.example.potatoservice.model.remote.MarkerData
import com.example.potatoservice.model.remote.Review
import com.example.potatoservice.model.remote.SendSignUpUserInfo
import com.example.potatoservice.model.remote.SidoGungu
import com.example.potatoservice.model.remote.VolunteerHistoryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    /* 로그인
    * AccessToken 헤더에 넣고, 요청을 보내면 avatarInfo 및 jwt 반환
    * avatarInfo -> nickname, ageRange, experience, level 등
     */
    @GET("/api/v1/users/login/kakao")
    fun kakaoLogin(
        @Header("token") accessToken: String,
    ): Call<LoginRequest>

    /* 회원 가입
    * jwtToken 넣고, sendSignUpUser 객체에 담아서 보냄
     */
    @POST("/api/v1/avatars")
    @Headers("Content-Type: application/json")
    fun sendUserInfo(
        @Header("Authorization") jwtToken: String,
        @Body signUpInfo: SendSignUpUserInfo
    ): Call<Void>

    /* 활동 조회
    *
     */
    @GET("/api/v1/activities")
    fun getActivities(
        @Query("page") page: Int,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("sidoCode") sidoCode: Int? = null,
        @Query("sidoGunguCode") sidoGunguCode: Int? = null,
        @Query("beforeDeadlineOnly") beforeDeadlineOnly: Boolean? = null,
        @Query("teenPossibleOnly") teenPossibleOnly: Boolean? = null,
        @Query("category") category: String? = null,
        @Query("keyword") keyword: String? = null
    ): Call<ActivityResponse>

    /* 개별 활동 조회
    *
     */
    @GET("/api/v1/activities/{activity_id}")
    fun getActivityDetail(
        @Path("activity_id") activityId: Int
    ): Call<ActivityDetail>

    //시도 목록 받음
    @GET("/api/v1/districts/sido")
    fun getSido(): Call<List<SidoGungu>>

    //군구 목록 받음
    @GET("/api/v1/districts/gungu")
    fun getGungu(): Call<List<SidoGungu>>

    //카테고리 목록 받음
    @GET("/api/v1/activities/categories")
    fun getCategory(): Call<List<String>>

    //회원 정보 받아오기
//    @GET("/api/v1/avatars")
//    fun getUserInfo() : Call<UserInfo>

    //개인 봉사 내역 받아오기
    @GET("/api/v1/histories")
    fun getHistory(
        @Header("Authorization") jwtToken: String
    ) : Call<VolunteerHistoryResponse>

    //리뷰 내용 받아오기
    @GET("/api/v1/reviews/questions")
    fun getReview(): Call<List<Review>>

    //개인 봉사 내역 추가하기
    @POST("/api/v1/histories")
    fun addHistory(
        @Header("Authorization") jwtToken: String,
        @Body actId : Int
    ): Call<Void>



    // 지도 맵 마커 -> 삭제
    @GET("/api/markers")
    fun getMarkers(
    ): Call<List<MarkerData>>



    // 카카오 맵 API
    @GET("v2/local/search/address.json")
    fun searchAddress(
        @Header("Authorization") apiKey: String,
        @Query("query") address: String
    ): Call<AddressResponse>


}

