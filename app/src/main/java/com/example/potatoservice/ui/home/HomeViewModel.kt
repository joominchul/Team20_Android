package com.example.potatoservice.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.potatoservice.model.remote.SidoGungu
import com.example.potatoservice.ui.share.SpinnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val homeRepository: HomeRepository,
    private val spinnerRepository: SpinnerRepository
) : AndroidViewModel(application) {

    //검색 결과 개수
    val numberOfElements: LiveData<Int> get() = homeRepository.numberOfElements

    //검색 결과 로딩 변수
    val searchLoading = homeRepository.loading

    //마지막 페이지인지 알려주는 변수
    val lastPage: LiveData<Boolean> get() = homeRepository.lastPage


    //지역 대분류
    val sidoList: LiveData<List<SidoGungu>> = spinnerRepository.sidoList
    val sidoLoading: LiveData<Boolean> = spinnerRepository.sidoLoading
    fun searchSidoList() {
        spinnerRepository.searchSidoList()
    }

    //지역 소분류
    val gunguList: LiveData<List<SidoGungu>> = spinnerRepository.gunguList
    val gunguLoading: LiveData<Boolean> = spinnerRepository.gunguLoading
    fun searchGunguList() {
        spinnerRepository.searchGunguList()
    }

    //시도 코드를 키로 하고, 군구 이름 리스트를 값으로 하는 Map 반환
    fun mappingGunguCode(): MutableMap<Int, MutableList<List<Any>>> {
        val gunguCodeMap: MutableMap<Int, MutableList<List<Any>>> = mutableMapOf(
            0 to mutableListOf(
                listOf("지역 소분류", 0)
            )
        )
        gunguList.value?.forEach { sidoGungu ->
            val sidoCode = sidoGungu.sidoCode
            val gunguName = sidoGungu.gunguName
            val gunguCode = sidoGungu.sidoGunguCode
            if (!gunguCodeMap.containsKey(sidoCode)) {
                gunguCodeMap[sidoCode] = mutableListOf(listOf("지역 소분류", 0))
            }
            gunguCodeMap[sidoCode]?.add(listOf(gunguName!!, gunguCode))
        }
        //세종시 추가
        gunguCodeMap[5690000] = mutableListOf(listOf("지역 소분류", 0))
        return gunguCodeMap
    }

    //봉사 활동 카테고리
    val categoryList: LiveData<List<String>> = spinnerRepository.categoryList
    val categoryLoading: LiveData<Boolean> = spinnerRepository.categoryLoading
    fun searchCategoryList() {
        spinnerRepository.searchCategoryList()
    }


}

