package com.example.potatoservice

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.model.remote.AvatarInfo
import com.example.potatoservice.ui.home.HomeRepository
import com.example.potatoservice.ui.mypage.MyPageModel
import com.example.potatoservice.ui.share.Request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    //봉사 활동 검색 결과 리스트
    private val _searchResults = MutableLiveData<List<Activity>>()
    val searchResults: LiveData<List<Activity>> get() = _searchResults

    //jwt토큰
    private val _jwtToken = MutableLiveData<String>()
    val jwtToken: LiveData<String> get() = _jwtToken

    //유저 정보
    private val _userInfo = MutableLiveData<AvatarInfo>()
    val userInfo: LiveData<AvatarInfo> get() = _userInfo

    private var _page = 0
    val page: Int get() = _page

    //유저 정보 세팅 (레벨, 닉네임, 히스토리...)
    fun setUserInfo(userInfo: AvatarInfo){
        MyPageModel.setMyPageModel(userInfo)
        MyPageModel.getMyPageList(_jwtToken.value!!)
    }

    fun searchHomeData(request: Request) {
        _page = 0
        val newRequest = request.copy(page = _page)
        homeRepository.search(newRequest)
        Log.d("testt", "검색결과: $newRequest")
    }

    /* 아바타 정보 공유
    * SignInActivity 에서 ViewModel에 넣었던 값 받아오기
     */
    fun setLoginData(token: String, userInfo: AvatarInfo) {
        _jwtToken.value = token
        _userInfo.value = userInfo
        Log.d("testt", "뷰모델 로그인 저장 : ${_userInfo.value}, ${_jwtToken.value}")
        setUserInfo(userInfo)
    }

    //다음 페이지 검색 함수
    fun loadMoreActivities(request: Request) {
        _page += 1
        val newRequest = request.copy(page = _page)
        viewModelScope.launch {
            homeRepository.loadMoreActivities(newRequest)
            Log.d("testt", "로딩결과: $newRequest")
        }
    }

    /* 봉사 활동 정보 공유
    * 홈 프레그먼트에서 검색한 활동을 받아오고 
    * 그 후에 맵 프레그먼트에서 관찰할 것
     */
    init {
        homeRepository.activityList.asLiveData().observeForever { activities ->
            _searchResults.value = activities
            Log.d("testt", activities.toString())
        }
    }

    /*
    스피너 선택 값 저장
    다른 프래그먼트로 이동해도 선택 값이 그대로 유지되게 함.
     */
    var spinnerSortValue = 0
    var spinnerMajorValue = 0
    var spinnerMinorValue = 0
    var spinnerCategoryValue = 0
    var spinnerAgeValue = 0
}
