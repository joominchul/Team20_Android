package com.example.potatoservice.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.model.remote.ActivityDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailRepository @Inject constructor(
	private val detailSearchData: DetailSearchData
){
	//상세 페이지 부분
	private var _activityDetail = MutableStateFlow<ActivityDetail?>(null)
	val activityDetail: StateFlow<ActivityDetail?> = _activityDetail.asStateFlow()
	//로딩 중인지 알려주는 변수
	private val _loading = MutableLiveData<Boolean>()
	val loading: LiveData<Boolean> get() = _loading
	suspend fun lookDetail(id:Int){
		coroutineScope {
			launch(Dispatchers.IO){
				_loading.postValue(true)
				detailSearchData.lookDetail(id, object : DetailSearchData.DetailCallback {
					override fun onLoaded(activityDetail: ActivityDetail) {
						_activityDetail.value = activityDetail
						_loading.postValue(false)
					}

					override fun onFailed() {
						_activityDetail.value = ActivityDetail.nullActivityDetail
						_loading.postValue(false)
					}
				})
			}
		}
	}
}