package com.example.potatoservice.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.ui.share.Request
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	application: Application,
	private val repository: HomeRepository
): AndroidViewModel(application) {

	var title = "봉사 활동 제목"
	var organization = "봉사 기관 이름"
	var category = "봉사 분야"
	var recruitmentPeriod = "모집 기간"
	var recruitmentNumber = "모집 인원"
	var time = "활동 기간"
	var serviceRecognitionTime = "봉사 인정 시간"
	var location = "상세 장소"
	var status = "확정 상태"

	val activityList:LiveData<List<Activity>> get() = repository.activityList.asLiveData()
	val searchResultCount = activityList.value?.count()
	val searchResultCountText = "총 " + searchResultCount.toString() + "건"

	//정렬
	val sortList = listOf("최신순", "거리순", "마감 임박순")
	//지역 대분류
	val majorRegoinList = listOf("지역 대분류", "경기", "대구", "서울")
	//지역 소분류
	var minorRegoinList = listOf(
		listOf("지역 소분류"), listOf("지역 소분류", "남양주", "성남"),
		listOf("지역 소분류","달성구", "동구"), listOf("지역 소분류", "강남구", "종로구"))
	//봉사 분야
	val volunteerList = listOf("봉사 분야", "생활지원 및 주거환경 개선", "교육 및 멘토링", "행정 및 사무지원",
		"문화, 환경 및 국제협력 활동", "보건의료 및 공익활동", "상담 및 자원봉사 교육", "기타 활동")
	//봉사 분야 API 이름
	val volunteerListAPI = listOf("LIFE_SUPPORT_AND_HOUSING_IMPROVEMENT", "EDUCATION_AND_MENTORING",
		"ADMINISTRATIVE_AND_OFFICE_SUPPORT", "CULTURE_ENVIRONMENT_AND_INTERNATIONAL_COOPERATION",
		"HEALTHCARE_AND_PUBLIC_WELFARE", "COUNSELING_AND_VOLUNTEER_TRAINING", "OTHER_ACTIVITIES")
	//나이 제한
	val ageList = listOf("나이 제한 없음", "청소년만", "성인만")
	val loading = repository.loading
	//검색 기능
	fun search(
		request: Request
	) {
		repository.search(request)
	}
}