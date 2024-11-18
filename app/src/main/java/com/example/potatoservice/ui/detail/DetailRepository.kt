package com.example.potatoservice.ui.detail

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.SplashActivity
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.ActivityDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

	fun addHistory(jwt : String ,actId : Int){
		RetrofitClient.apiService().addHistory("Bearer $jwt", actId)
			.enqueue(object : Callback<Void> {
				override fun onResponse(call: Call<Void>, response: Response<Void>) {
					when (response.code()) {
						201 -> {
							Log.d("seyoung","활동 추가 성공")
						}

						401 -> {
							Log.d("seyoung","인증 오류")
						}

						500 -> {
							Log.d("seyoung","서버 내부 오류")
						}


						else -> {
							val errorBody = response.errorBody()?.string()
							Log.d(
								"seyoung",
								"Failure Response: ${errorBody ?: "No error message"}"
							)
						}
					}
				}

				override fun onFailure(call: Call<Void>, t: Throwable) {
					Log.d("seyoung", "Request Failed: ${t.message}")
				}
			})
	}
}