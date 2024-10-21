package com.example.potatoservice.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
	application: Application,
	private val repository: DetailRepository
): AndroidViewModel(application){
	//리뷰 퍼센트지 값
	var review1 = "50"
	var review2 = "40"
	var review3 = "30"

	val activityDetail = repository.activityDetail.asLiveData()

	//id에 맞는 봉사 활동 상세 정보를 가져 오는 함수
	fun getDetail(id: Int){
		viewModelScope.launch(Dispatchers.IO) {
			 repository.lookDetail(id)
		}
		Log.d("testt", "viewModelScope ${activityDetail.value?.actTitle}")
	}

	private val _agePossible = MutableLiveData<String>()
	val agePossible: LiveData<String> = _agePossible
	//나이 제한 업데이트
	fun setAgePossible(){
		_agePossible.value = when {
			activityDetail.value == null -> "세부 정보 없음"
			activityDetail.value!!.teenPossible && activityDetail.value!!.adultPossible -> "모두 가능"
			activityDetail.value!!.teenPossible && !activityDetail.value!!.adultPossible -> "청소년만 가능"
			!activityDetail.value!!.teenPossible && activityDetail.value!!.adultPossible -> "성인만 가능"
			else -> "모두 불가능" }
	}

	private val _groupPossible = MutableLiveData<String>()

	val groupPossible:  LiveData<String> = _groupPossible
	fun setGroupPossible() {
		_groupPossible.value = when {
			activityDetail.value == null -> "세부 정보 없음"
			activityDetail.value!!.groupPossible -> "가능"
			else -> "불가능"
		}
	}

	val loading = repository.loading

}