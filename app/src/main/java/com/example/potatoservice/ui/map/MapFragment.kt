package com.example.potatoservice.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.potatoservice.MainViewModel
import com.example.potatoservice.R
import com.example.potatoservice.databinding.FragmentMapBinding
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.model.remote.MarkerData
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapAuthException
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import android.location.Location as AndroidLocation

class MapFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var kakaoMap: KakaoMap? = null
    private val mapViewModel: MapViewModel by viewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initMap()
        //현재 위치 버튼
        binding.buttonCurrentLocation.setOnClickListener { moveToCurrentLocation() }

        // MainViewModel에서 검색 결과를 관찰하여 각 활동의 위치를 주소로 전달
        mainViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            searchResults?.let { activities ->
                mapViewModel.clearMarkerDataList()  // 이전 마커 데이터 초기화
                mapViewModel.fetchCoordinatesList(activities)  // 전체 활동 데이터를 전달하여 좌표를 변환
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false).apply {
            viewModel = this@MapFragment.mapViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    private fun initMap() {
        KakaoMapSdk.init(requireContext(), getString(R.string.kakao_api_key))
        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() { }
            override fun onMapError(error: Exception) {
                val errorMessage = when ((error as? MapAuthException)?.errorCode) {
                    401 -> getString(R.string.error_auth_failure)
                    499 -> getString(R.string.error_server_communication_failure)
                    else -> getString(R.string.error_unknown)
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                this@MapFragment.kakaoMap = kakaoMap

                // 마지막 저장된 카메라 위치 설정
                val lastLocation = mapViewModel.getLastLocation()
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(
                    LatLng.from(lastLocation.first, lastLocation.second), lastLocation.third
                )
                kakaoMap.moveCamera(cameraUpdate)

                // 카메라 이동 종료 리스너
                kakaoMap.setOnCameraMoveEndListener { _, _, _ ->
                    val position = kakaoMap.cameraPosition!!.position
                    val currentZoom = kakaoMap.cameraPosition!!.zoomLevel
                    mapViewModel.saveLastLocation(
                        position.latitude,
                        position.longitude,
                        currentZoom
                    )
                }

                // 지도 클릭 리스너 추가 -> 카드뷰 숨김
                kakaoMap.setOnMapClickListener { _, _, _, _ ->
                    hideCardView()
                }

                // markerDataList 관찰
                mapViewModel.markerDataList.observe(viewLifecycleOwner) { markerDataList ->
                    // 상세 페이지에서 받아온 정보가 없을 때만
                    if (arguments == null){
                        markerDataList?.let {
                            mapViewModel.addMarkersToMap(kakaoMap)
                        }
                    }
                }

                //선택된 마커가 있으면 카드뷰 업데이트, null이면 카드뷰를 숨김
                mapViewModel.selectedMarker.observe(viewLifecycleOwner) { markerData ->
                    markerData?.let {
                        updateCardView(it)
                        showCardView()
                    } ?: hideCardView()
                }


                // 디테일에서 기관 정보 얻음
                getInstituteLocation(kakaoMap)
            }
        })
    }

    /* 현재 위치 이동 버튼
     */
    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener { location: AndroidLocation? ->
            location?.let {
                val curLat = it.latitude
                val curLon = it.longitude
                val latLng = LatLng.from(curLat, curLon)
                val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng)
                kakaoMap?.moveCamera(cameraUpdate)
            } ?: run {
                Toast.makeText(requireContext(), "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 카드뷰에 정보를 업데이트하는 함수
    private fun updateCardView(markerData: MarkerData) {
        binding.titleText.text = markerData.title
        binding.serviceOrganizationServiceCategory.text = markerData.organization
        binding.serviceRecruitment.text = "[모집 기간] ${markerData.recruitmentPeriod}\n[모집 인원] ${markerData.recruitmentCount} 명"
        binding.serviceTime.text = "[활동 기간] ${markerData.activityPeriod}\n[활동 시간] ${markerData.activityTime} 시"
        binding.descriptionText.text = "[주소] ${markerData.address}\n[활동 설명] ${markerData.description}"
    }

    // CardView를 보이게 설정
    private fun showCardView() {
        binding.infoCardView.visibility = View.VISIBLE
    }

    // CardView를 숨김
    private fun hideCardView() {
        binding.infoCardView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 디테일에서 기관 정보 얻음
    private fun getInstituteLocation(kakaoMap: KakaoMap) {
        val name = this.arguments?.getString("name") ?: "기관명"
        val latitude = this.arguments?.getDouble("latitude")
        val longitude = this.arguments?.getDouble("longitude")
        if (latitude != null && longitude != null) {
            val latLng = LatLng.from(latitude, longitude)
            mapViewModel.addInstituteMarker(kakaoMap, latLng, name)
            mapViewModel.moveInstitute(kakaoMap, latLng)
        }
    }
}
