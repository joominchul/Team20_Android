package com.example.potatoservice.ui.mypage

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.R

class MyPageModel(context : Context) {

    // DialogModel 배열을 생성
    val dialogArray = arrayOf(
        DialogModel(
            title = "리뷰 요청",
            content = context.getString(R.string.question_1),
            imageBackground = R.drawable.ic_hotgamja_main_character,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = context.getString(R.string.question_2),
            imageBackground = R.drawable.ic_interest_cultural_event,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = context.getString(R.string.question_3),
            imageBackground = R.drawable.ic_interest_education,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = context.getString(R.string.question_4),
            imageBackground = R.drawable.ic_interest_international_event,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),
        DialogModel(
            title = "리뷰 요청",
            content = context.getString(R.string.question_5),
            imageBackground = R.drawable.ic_interest_support,
            positiveButtonText = "네",
            negativeButtonText = "아니오"
        ),

        )

    //mypage 보기방식 spinner item
    val spinnerItems : Array<String> = arrayOf("전체보기", "신청완료", "확정 대기", "수행완료됨")

    //봉사 시간 데이터
    val volunteerHousr = MutableLiveData<Int>()
    val volunteerCount = MutableLiveData<Int>()

    fun setVolunteerHours(){
        volunteerHousr.value = 199
    }

    fun setVolunteerCount(){
        volunteerCount.value = 190
    }


    //개인 봉사내역 데이터

}