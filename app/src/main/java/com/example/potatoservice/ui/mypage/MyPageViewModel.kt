package com.example.potatoservice.ui.mypage

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.potatoservice.R

class MyPageViewModel(private val context: Context, private val myPageModel: MyPageModel) : ViewModel() {

    //봉사시간
    private val _vmVolunteerHours = MutableLiveData<Int>()
    val vmVolunteerHours: LiveData<Int> get() = _vmVolunteerHours

    //봉사 건수
    private val _vmVolunteerCount = MutableLiveData<Int>()
    val vmVolunteerCount: LiveData<Int> get() = _vmVolunteerCount

    //progressbar value
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    //progressbar 퍼센트
    private val _progressPercent = MutableLiveData<Int>()
    val progressPercent : LiveData<Int> get() = _progressPercent

    //레벨
    private val _vmLevel = MutableLiveData<Int>()
    val vmLevel : MutableLiveData<Int> get() = _vmLevel

    //다이얼로그
    private val _vmDialogArray: Array<DialogModel> = myPageModel.dialogArray
    val vmDialogArray: Array<DialogModel> get() = _vmDialogArray

    //스피너
    private val vmSpinnerItems: Array<String> = myPageModel.spinnerItems
    var vmSpinnerAdapter: ArrayAdapter<String>


    init {
        vmSpinnerAdapter = ArrayAdapter(context, R.layout.spinner_item, vmSpinnerItems)
        vmSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        myPageModel.volunteerHousr.observeForever {
            _vmVolunteerHours.value = it
            calculateEx(it)
        }
        myPageModel.volunteerCount.observeForever {
            _vmVolunteerCount.value = it
        }
    }


    private fun calculateEx(hours: Int) {
        //봉사시간 10시간마다 레벨 업
        val level = hours / 10
        val progressValue = (hours % 10)*10
        _vmLevel.value = level
        _progress.value = progressValue
        _progressPercent.value = progressValue
    }

    fun setVolunteerHours(){
        myPageModel.setVolunteerHours()
    }

    fun setVolunteerCount(){
        myPageModel.setVolunteerCount()
    }

}
