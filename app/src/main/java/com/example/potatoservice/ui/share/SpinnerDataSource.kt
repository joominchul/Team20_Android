package com.example.potatoservice.ui.share

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.potatoservice.model.APIService
import com.example.potatoservice.model.remote.SidoGungu
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SpinnerDataSource @Inject constructor(private val apiService: APIService){
	private val _sidoList = MutableLiveData<List<SidoGungu>>()
	val sidoList get() = _sidoList
	private val _sidoLoading = MutableLiveData(true)
	val sidoLoading get() = _sidoLoading
	fun getSidoList() {
		apiService.getSido().enqueue(
			object : Callback<List<SidoGungu>>{
				override fun onResponse(
					call: Call<List<SidoGungu>>,
					response: Response<List<SidoGungu>>
				) {
					if (response.isSuccessful){
						Log.d("seyoung",response.body().toString())
						_sidoList.value = response.body()?.map { sidoGungu ->
							SidoGungu(
								sidoGungu.sidoGunguCode,
								sidoGungu.sidoCode,
								sidoGungu.sidoName,
								sidoGungu.gunguName,
								sidoGungu.sido
							)
						}
						_sidoLoading.value = false
					}else{
						Log.e("testt", "sido onResponse fail: ${response.code()}")
						_sidoList.value = emptyList()
						_sidoLoading.value = false
					}
				}

				override fun onFailure(call: Call<List<SidoGungu>>, t: Throwable) {
					Log.e("testt", "sido onFailure: ${t.message}")
					_sidoList.value = emptyList()
					_sidoLoading.value = false
				}

			}
		)
	}
	//군구 리스트 받기
	private val _gunguList = MutableLiveData<List<SidoGungu>>()
	val gunguList get() = _gunguList
	private val _gunguLoading = MutableLiveData(true)
	val gunguLoading get() = _gunguLoading
	fun getGunguList() {
		apiService.getGungu().enqueue(
			object : Callback<List<SidoGungu>>{
				override fun onResponse(
					call: Call<List<SidoGungu>>,
					response: Response<List<SidoGungu>>
				) {
					if (response.isSuccessful){
						_gunguList.value = response.body()?.map { sidoGungu ->
							SidoGungu(
								sidoGungu.sidoGunguCode,
								sidoGungu.sidoCode,
								sidoGungu.sidoName,
								sidoGungu.gunguName,
								sidoGungu.sido
							)
						}
						_gunguLoading.value = false
					}else{
						Log.e("testt", "gungu onResponse fail: ${response.code()}")
						_gunguList.value = emptyList()
						_gunguLoading.value = false
					}
				}

				override fun onFailure(call: Call<List<SidoGungu>>, t: Throwable) {
					Log.e("testt", "gungu onFailure: ${t.message}")
					_gunguList.value = emptyList()
					_gunguLoading.value = false
				}

			}
		)
	}
	//카테고리 리스트 받기
	private val _categoryList = MutableLiveData<List<String>>()
	val categoryList get() = _categoryList
	private val _categoryLoading = MutableLiveData(true)
	val categoryLoading get() = _categoryLoading
	fun getCategoryList() {
		apiService.getCategory().enqueue(
			object : Callback<List<String>>{
				override fun onResponse(
					call: Call<List<String>>,
					response: Response<List<String>>
				) {
					if (response.isSuccessful){
						_categoryList.value = response.body()
						_categoryLoading.value = false
					}else{
						Log.e("testt", "category onResponse fail: ${response.code()}")
						_categoryList.value = emptyList()
						_categoryLoading.value = false
					}
				}

				override fun onFailure(call: Call<List<String>>, t: Throwable) {
					Log.e("testt", "category onFailure: ${t.message}")
					_categoryList.value = emptyList()
					_categoryLoading.value = false
				}

			}
		)
	}
}