package com.example.potatoservice.ui.home

import com.example.potatoservice.model.APIService
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.model.remote.ActivityResponse
import com.example.potatoservice.ui.share.Request

import com.example.potatoservice.ui.share.SpinnerList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
//레트로핏으로 데이터 받아 오는 클래스
class HomeSearchDataSource @Inject constructor(private val apiService: APIService):HomeSearchData{

	private fun ActivityResponse.toActivityList(): List<Activity> {
		return content.map { content ->
			Activity(
				content.actId,
				content.actTitle,
				content.actLocation,
				content.noticeStartDate?.substring(2, 10),
				content.noticeEndDate.substring(2, 10),
				content.actStartDate?.substring(2, 10),
				content.actEndDate.substring(2, 10),
				content.actStartTime,
				content.actEndTime,
				content.recruitTotalNum,
				content.category
			)
		}
	}

	override fun search(request: Request, callback: HomeSearchData.LoadCallback){
		with(request){
			apiService.getActivities(page, size, sort, sidoCode, sidoGunguCode, beforeDeadlineOnly, teenPossibleOnly, category, keyword).enqueue(

				object : Callback<ActivityResponse>{
					override fun onResponse(
						call: Call<ActivityResponse>,
						response: Response<ActivityResponse>
					) {
						if (response.isSuccessful){
							val activityList = response.body()?.toActivityList()?: emptyList()
							val numberOfElements = response.body()?.numberOfElements?:0
							val last = response.body()?.last?:true
							callback.onLoaded(activityList, numberOfElements, last)
						} else{
							callback.onFailed()
						}
					}

					override fun onFailure(call: Call<ActivityResponse>, t: Throwable) {
						callback.onFailed()
					}

				}
			)
		}

	}

}