package com.example.potatoservice.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potatoservice.R
import com.example.potatoservice.model.KakaoRetrofitClient
import com.example.potatoservice.model.RetrofitClient
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.model.remote.AddressResponse
import com.example.potatoservice.model.remote.MarkerData
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapViewModel : ViewModel() {

    private val _cameraPosition = MutableLiveData<LatLng>()
    private val _zoomLevel = MutableLiveData<Int>()

    fun saveLastLocation(latitude: Double, longitude: Double, zoom: Int) {
        _cameraPosition.value = LatLng.from(latitude, longitude)
        _zoomLevel.value = zoom
    }

    fun getLastLocation(): Triple<Double, Double, Int> {
        val lat = _cameraPosition.value?.latitude ?: 37.402005
        val lon = _cameraPosition.value?.longitude ?: 127.108621
        val zoom = _zoomLevel.value ?: 15
        return Triple(lat, lon, zoom)
    }

    private val _markerDataList = MutableLiveData<List<MarkerData>>(listOf())
    val markerDataList: LiveData<List<MarkerData>> get() = _markerDataList

    fun clearMarkerDataList() {
        _markerDataList.value = listOf()
    }

    fun addMarkerData(lat: Double, lng: Double, title: String, address: String) {
        val currentList = _markerDataList.value.orEmpty().toMutableList()
        val newMarker = MarkerData(
            lat = lat,
            lng = lng,
            title = title,
            address = address,
            description = "설명",
            organization = "기관",
            recruitmentPeriod = "모집기간",
            recruitmentCount = "모집인원",
            activityTime = "활동 시간",
            activityPeriod = "활동 기간"
        )
        currentList.add(newMarker)
        _markerDataList.value = currentList
    }

    private val _selectedMarker = MutableLiveData<MarkerData?>()
    val selectedMarker: LiveData<MarkerData?> get() = _selectedMarker

    fun setMarkerData() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.apiService().getMarkers()
            response.enqueue(object : Callback<List<MarkerData>> {
                override fun onResponse(call: Call<List<MarkerData>>, response: Response<List<MarkerData>>) {
                    if (response.isSuccessful) {
                        _markerDataList.postValue(response.body())
                        Log.d("testt", "Markers fetched successfully: ${response.body()}")
                    } else {
                        Log.d("testt", "Response unsuccessful: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<MarkerData>>, t: Throwable) {
                    Log.e("testt", "Failed to fetch markers", t)
                }
            })
        }
    }

    fun selectMarker(markerData: MarkerData) {
        _selectedMarker.value = markerData
    }

    fun clearSelectedMarker() {
        _selectedMarker.value = null
    }

    fun addMarkersToMap(kakaoMap: KakaoMap) {
        kakaoMap.labelManager?.removeAllLabelLayer()

        val markerDataList = _markerDataList.value ?: return

        for (markerData in markerDataList) {
            val latLng = LatLng.from(markerData.lat, markerData.lng)
            val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_map_marker).setZoomLevel(5))
            val labelOptions = LabelOptions.from(latLng).setStyles(styles)
            val label = kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
            label.tag = markerData
        }

        kakaoMap.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(kakaoMap: KakaoMap, layer: LabelLayer, clickedLabel: com.kakao.vectormap.label.Label) {
                val markerData = clickedLabel.tag as? MarkerData
                markerData?.let {
                    selectMarker(it)
                }
            }
        })
    }
    //상세 페이지 지도 크게 보기를 통한 기관 마커 생성
    fun addInstituteMarker(kakaoMap: KakaoMap, latLng: LatLng, instituteName: String) {
        val style = LabelStyle.from(R.drawable.ic_map_marker_institute).setZoomLevel(5)
            .setTextStyles(LabelTextStyle.from(40, R.color.point_brown_2))
        val labelOptions = LabelOptions.from(latLng).setStyles(style).setTexts(instituteName)
        kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)
    }
    //상세 페이지 지도 크게 보기를 통한 기관 위치로 이동
    fun moveInstitute(kakaoMap: KakaoMap, latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
        kakaoMap.moveCamera(cameraUpdate)
    }

    fun fetchCoordinatesList(activities: List<Activity>) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredCoordinates = activities.map { activity ->
                async {
                    fetchCoordinates(activity)
                }
            }

            val results = deferredCoordinates.awaitAll().filterNotNull()
            _markerDataList.postValue(results)
        }
    }

    private suspend fun fetchCoordinates(activity: Activity): MarkerData? {
        val apiKey = "KakaoAK aa23edc0dd8f4cc31ed3c9245040e78d"
        val apiService = KakaoRetrofitClient.apiService()

        return suspendCoroutine { continuation ->
            apiService.searchAddress(apiKey, activity.actLocation.toString()).enqueue(object : Callback<AddressResponse> {
                override fun onResponse(call: Call<AddressResponse>, response: Response<AddressResponse>) {
                    if (response.isSuccessful) {
                        val documents = response.body()?.documents
                        if (!documents.isNullOrEmpty()) {
                            val firstResult = documents[0]
                            val lat = firstResult.y.toDoubleOrNull()
                            val lng = firstResult.x.toDoubleOrNull()
                            continuation.resume(
                                MarkerData(
                                    lat = lat ?: 0.0,
                                    lng = lng ?: 0.0,
                                    title = activity.actTitle.toString(),
                                    address = activity.actLocation.toString(),
                                    description = "활동 설명: ${activity.category}",
                                    organization = "",
                                    recruitmentPeriod = "${activity.noticeStartDate} ~ ${activity.noticeEndDate}",
                                    recruitmentCount = "${activity.recruitTotalNum}",
                                    activityTime = "${activity.actStartTime} ~ ${activity.actEndTime}",
                                    activityPeriod = "${activity.actStartDate} ~ ${activity.actEndDate}"
                                )
                            )
                        } else {
                            continuation.resume(null)
                        }
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
                    continuation.resume(null)
                }
            })
        }
    }
}
