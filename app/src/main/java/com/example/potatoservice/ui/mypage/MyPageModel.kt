package com.example.potatoservice.ui.mypage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.R
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.AvatarInfo
import com.example.potatoservice.model.remote.Review
import com.example.potatoservice.model.remote.ReviewRequest
import com.example.potatoservice.model.remote.VolunteerHistoryResponse
import com.example.potatoservice.ui.share.Volunteer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MyPageModel {

    val dialogModels = MutableLiveData<List<DialogModel>>()
    val reviewHistoryId = MutableLiveData<Int>()

    //mypage 보기방식 spinner item
    val spinnerItems : Array<String> = arrayOf("전체보기", "신청완료", "확정 대기", "수행완료됨")

    //봉사 시간 데이터
    val volunteerHours = MutableLiveData<Int>()
    val volunteerCount = MutableLiveData<Int>()
    val ninkname = MutableLiveData<String>()
    val level = MutableLiveData<Int>()

    //리사이클러뷰 count
    val recyclerViewCount = MutableLiveData<Int>()

    //봉사 내역 리스트
    val volunteerHistoryList = MutableLiveData<List<Volunteer>>()

    //봉사 내역 서버로부터 받기
    fun getMyPageList(jwtToken: String){
        RetrofitClient.apiService().getHistory("Bearer $jwtToken").enqueue(object : Callback<VolunteerHistoryResponse> {
            override fun onResponse(
                call: Call<VolunteerHistoryResponse>,
                response: Response<VolunteerHistoryResponse>
            ) {
                if (response.isSuccessful) {
                    // 서버에서 받은 VolunteerHistoryResponse에서 content를 가져옴
                    val historyItems = response.body()?.content ?: emptyList()


                    // HistoryItem을 Volunteer로 변환
                    volunteerHistoryList.value = historyItems.map { historyItem ->
                        Volunteer(
                            id = historyItem.historyId,
                            title = historyItem.activity.actTitle ?: "제목 없음",
                            institution = " ",
                            Category = historyItem.activity.category,
                            recruitmentPeriod = "${historyItem.activity.noticeStartDate} ~ ${historyItem.activity.noticeEndDate}",
                            recruitmentCount = historyItem.activity.recruitTotalNum,
                            activityPeriod = "${historyItem.activity.actStartDate} ~ ${historyItem.activity.actEndDate}",
                            volunteerHours = " ", // 봉사시간 계산 필요
                            address = historyItem.activity.actLocation ?: "주소 없음",
                            status = historyItem.activityStatus
                        )
                    }

                }
                else{
                    Log.d("seyoung","getMyPageList response.isSuccessful 실패")
                }
            }

            override fun onFailure(call: Call<VolunteerHistoryResponse>, t: Throwable) {
                // 실패 처리
                Log.d("seyoung","MyPageModel에서 getMyPageList가 실패함")
            }
        })

    }

    //클릭한 히스토리 아이디 설정
    fun setReviewHistoryId(id: Int) {
        reviewHistoryId.value = id
    }

    //리뷰 질문 내용 서버로부터 받기
    fun getReviewQuestions() {
        RetrofitClient.apiService().getReview().enqueue(object : Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                if (response.isSuccessful) {
                    Log.d("seyoung", "MyPageModel에서 getReviewQuestions 성공 body :${response.body()}")
                    response.body()?.let { questions ->
                        val models = questions.map { question ->
                            DialogModel(
                                title = "Review",  // 기본 타이틀
                                content = question.content,
                                previousButtonText = "이전",
                                nextButtonText = "다음"
                            )
                        }
                        dialogModels.value = models
                    }
                }
            }

            override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                // 실패 시 처리 로직
            }
        })
    }

    // MyPageModel
    fun sendReview(jwtToken: String, reviewRequest: ReviewRequest) {
        RetrofitClient.apiService().sendReview("Bearer $jwtToken", reviewRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 성공 시 처리
                    Log.d("seyoung", "Review successfully sent.")
                } else {
                    // 실패 시 처리
                    Log.e("seyoung", "Failed to send review: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 실패 시 처리
                Log.e("seyoung", "Network error: ${t.message}")
            }
        })
    }



    fun setMyPageModel(userInfo: AvatarInfo?){
        volunteerHours.value = userInfo?.avatarExp
        ninkname.value = userInfo?.nickName
        level.value = userInfo?.avatarLevel
    }



}