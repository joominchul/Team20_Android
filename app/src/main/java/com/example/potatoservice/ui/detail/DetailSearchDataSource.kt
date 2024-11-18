package com.example.potatoservice.ui.detail

import android.util.Log
import com.example.potatoservice.model.APIService
import com.example.potatoservice.model.remote.ActivityDetail
import com.example.potatoservice.model.remote.ActivityDetail.Companion.nullActivityDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DetailSearchDataSource @Inject constructor(
	private val apiService: APIService
) : DetailSearchData{

	private fun ActivityDetail.toActivityDetail(): ActivityDetail {
		return ActivityDetail(
			actId,
			actTitle,
			actLocation,
			description,
			noticeStartDate?.substring(0, 10),
			noticeEndDate.substring(0, 10),
			actStartDate?.substring(0, 10),
			actEndDate.substring(0, 10),
			actStartTime,
			actEndTime,
			recruitTotalNum,
			adultPossible,
			teenPossible,
			groupPossible,
			latitude,
			longitude,
			actWeek,
			actManager,
			actPhone,
			url,
			category,
			institute
		)
	}
	override fun lookDetail(id: Int, callback: DetailSearchData.DetailCallback){
		apiService.getActivityDetail(id).enqueue(
			object : Callback<ActivityDetail> {
				override fun onResponse(
					call: Call<ActivityDetail>,
					response: Response<ActivityDetail>
				) {
					if (response.isSuccessful){
						val body = response.body()
						val activityDetail = body?.toActivityDetail() ?:
						nullActivityDetail
						callback.onLoaded(activityDetail)

					} else{
						Log.d("testt", "detail onResponse fail")
						callback.onFailed()
					}
				}

				override fun onFailure(call: Call<ActivityDetail>, t: Throwable) {
					Log.d("testt", "detail fail : $t")
					callback.onFailed()
				}

			}
		)

	}
}