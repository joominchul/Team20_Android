package com.example.potatoservice.ui.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.potatoservice.R
import com.example.potatoservice.databinding.ActivityDetailBinding
import com.example.potatoservice.model.remote.ActivityDetail
import com.example.potatoservice.model.remote.Institute
import com.example.potatoservice.model.remote.Score
import com.example.potatoservice.ui.map.MapFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapAuthException
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import dagger.hilt.android.AndroidEntryPoint
//신청 url
const val requestUrl = "https://www.1365.go.kr/vols/1572247904127/partcptn/timeCptn.do?type=show&progrmRegistNo="
@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
	//봉사 활동 id
	private var id = 0
	private lateinit var binding: ActivityDetailBinding
	private lateinit var mapView: MapView
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private var curLat: Double = 0.0
	private var curLon: Double = 0.0
	private lateinit var kakaoMap: KakaoMap
	private lateinit var viewModel: DetailViewModel
	//기관 정보
	private var institute: Institute? = null
	//상세 정보
	private var detail: ActivityDetail? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
		val intent = intent
		//인텐트로 id 받아옴
		id = intent.getIntExtra("id", 0)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

		binding.viewmodel = viewModel
		getActivity(id)

		//전화걸기 버튼
		binding.callButton.setOnClickListener {
			viewModel.loading.observe(this, Observer {
				val phoneNumber = detail?.actPhone
				startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
			})
		}

		//신청하기 버튼
		binding.requestButton.setOnClickListener {
			val url = requestUrl + id
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
			if (intent.resolveActivity(packageManager) != null) {
				startActivity(intent)
			} else {
				Toast.makeText(this, "웹 브라우저 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
			}
			val sharedPref = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
			val jwt = sharedPref.getString("jwt_token","null")
			if(jwt != null){
				viewModel.addHistory(jwt, id)
			}

		}


		//지도 기능들
		setMapView()
		//지도 현위치 버튼
		binding.buttonCurrentLocation.setOnClickListener {
			moveToCurrentLocation()
		}
		//닫기 버튼
		binding.detailClose.setOnClickListener {
			finish()
		}
		showLoading()
		binding.mapSizeUpButton.setOnClickListener {
			mapSizeUp()
		}
	}
	//지도 페이지로 이동
	private fun mapSizeUp(){
		val bundle = Bundle()
		detail!!.latitude?.let { bundle.putDouble("latitude", it) }
		detail!!.longitude?.let { bundle.putDouble("longitude", it) }
		bundle.putString("name", institute!!.name)
		val fragment = MapFragment()
		fragment.arguments = bundle
		val manager = supportFragmentManager
		val transaction = manager.beginTransaction()
		transaction.replace(binding.frameLayout.id, fragment)
		transaction.addToBackStack(null)
		transaction.commit()
	}
	//받아온 id로 봉사 활동 데이터 얻음
	private fun getActivity(id: Int){
		viewModel.getDetail(id)
		viewModel.activityDetail.observe(this, Observer {activityDetail ->
			binding.detail = activityDetail
			binding.institute = activityDetail?.institute
			institute = activityDetail?.institute
			detail = activityDetail
			viewModel.setAgePossible()
			viewModel.setGroupPossible()
			setReview(institute?.scores)
			setRatingBar(institute?.scores)
			binding.invalidateAll()
		})
	}
	//리뷰 표시
	private fun setReview(scores: List<Score>?){
		if (scores != null) {
			viewModel.review1Question = scores[0].question.content
			viewModel.review2Question = scores[1].question.content
			viewModel.review3Question = scores[2].question.content
		}
		else{
			viewModel.review1Question = "질문 정보가 없습니다."
			viewModel.review2Question = "질문 정보가 없습니다."
			viewModel.review3Question = "질문 정보가 없습니다."
		}
	}
	//리뷰 평점 설정
	private fun setRatingBar(scores: List<Score>?) {
		if (scores != null) {
			viewModel.review1 = scores[0].score
			viewModel.review2 = scores[1].score
			viewModel.review3 = scores[2].score
		}
		binding.ratingBar1.setRating(viewModel.review1.toFloat())
		binding.ratingBar2.setRating(viewModel.review2.toFloat())
		binding.ratingBar3.setRating(viewModel.review3.toFloat())
	}
	//지도 기능들
	override fun onResume() {
		super.onResume()
		mapView.resume()
	}

	override fun onPause() {
		super.onPause()
		mapView.pause()
	}

	//카카오맵 초기 설정
	private fun setMapView(){
		mapView = binding.detailMapView
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

		mapView.start(object : MapLifeCycleCallback() {
			override fun onMapDestroy() {
			}

			override fun onMapError(error: Exception) {
				val errorMessage = when ((error as? MapAuthException)?.errorCode) {
					401 -> getString(R.string.error_auth_failure)
					499 -> getString(R.string.error_server_communication_failure)
					else -> getString(R.string.error_unknown)
				}
				Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
			}
		}, object : KakaoMapReadyCallback() {
			override fun onMapReady(kakaoMap: KakaoMap) {
				this@DetailActivity.kakaoMap = kakaoMap
				//기관 위치 지도에서 마커로 표시하고 카메라 이동.
				viewModel.loading.observe(this@DetailActivity, Observer {
					if (detail?.latitude != null && detail?.longitude != null){
						val latLng = LatLng.from(detail?.latitude!!, detail?.longitude!!)
						setInitialCameraPosition(latLng)
						setMarker(latLng)
					}
				})
			}
		})
		//스크롤뷰가 지도 터치 간섭 안 하게
		mapView.surfaceView?.setOnTouchListener(View.OnTouchListener(){
			view, motionEvent ->
			binding.scrollView.requestDisallowInterceptTouchEvent(true)
			false
		})
	}


	//봉사 활동 장소 마커로 표시
	private fun setMarker(latLng:LatLng) {
		val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_map_marker_institute).setZoomLevel(5))
		val labelOptions = LabelOptions.from(latLng).setStyles(styles)
		kakaoMap.labelManager!!.layer!!.addLabel(labelOptions)

	}
	//현재 위치 계산
	private fun currentLocation(onLocationRetrieved: (LatLng) -> Unit) {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
			ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
			return
		}

		fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
			location?.let {
				curLat = it.latitude
				curLon = it.longitude
				val latLng = LatLng.from(curLat, curLon)
//				Log.d("CurrentLocation", "위도: $curLat, 경도: $curLon")
				onLocationRetrieved(latLng) // 위치를 찾았을 때 콜백 호출
			} ?: run {
				Toast.makeText(this, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
			}
		}
	}

	// 버튼 클릭 시 현재 위치로 이동하는 함수
	private fun moveToCurrentLocation() {
		currentLocation { latLng ->
			val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
			kakaoMap.moveCamera(cameraUpdate) // 카메라를 현재 위치로 이동
			Toast.makeText(this, "현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
		}
	}
	// 기관 위치로 이동
	private fun setInitialCameraPosition(latLng: LatLng) {
		val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
		kakaoMap.moveCamera(cameraUpdate)
	}
	//로딩 화면 설정
	private fun showLoading(){
		viewModel.loading.observe(this, Observer {loading->
			if (loading){
				binding.main.visibility = View.GONE
				binding.loadingLayout.visibility = View.VISIBLE
				binding.loadingLayout.startShimmer()
			}else{
				binding.loadingLayout.stopShimmer()
				binding.loadingLayout.visibility = View.GONE
				binding.main.visibility = View.VISIBLE
			}
		})

	}
}