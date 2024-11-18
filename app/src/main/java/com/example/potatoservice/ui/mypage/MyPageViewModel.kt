package com.example.potatoservice.ui.mypage

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.potatoservice.R
import com.example.potatoservice.model.remote.AvatarInfo
import com.example.potatoservice.ui.share.Volunteer

class MyPageViewModel(private val context: Context) : ViewModel(), OnVolunteerClickListener {

    //닉네임
    private val _vmNickname = MutableLiveData<String>()
    val vmNickname: LiveData<String> get() = _vmNickname

    //봉사시간
    private val _vmVolunteerHours = MutableLiveData<Int>()
    val vmVolunteerHours: LiveData<Int> get() = _vmVolunteerHours

    //봉사 건수
    private val _vmVolunteerCount = MutableLiveData<Int>()
    val vmVolunteerCount: LiveData<Int> get() = _vmVolunteerCount


    //경험치바 값
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    //경험치바 퍼센트
    private val _progressPercent = MutableLiveData<Int>()
    val progressPercent: LiveData<Int> get() = _progressPercent

    //레벨
    private val _vmLevel = MutableLiveData<Int>()
    val vmLevel: MutableLiveData<Int> get() = _vmLevel

    //스피너
    private val vmSpinnerItems: Array<String> = MyPageModel.spinnerItems
    var vmSpinnerAdapter: ArrayAdapter<String>

    //리사이클러뷰 count
    private val _vmRecyclerViewCount = MutableLiveData<Int>()
    val vmRecyclerViewCount: LiveData<Int> get() = _vmRecyclerViewCount


    //리사이클러뷰 어댑터
    val vmVolunteerAdapter: VolunteerAdapter = VolunteerAdapter(
        MyPageModel.volunteerHistoryList.value ?: emptyList(), this
    )

    val vmReviewDialog = MutableLiveData<List<DialogModel>>()



    //초기 설정
    init {
        //mypage 스피너 설정
        vmSpinnerAdapter = ArrayAdapter(context, R.layout.spinner_item, vmSpinnerItems)
        vmSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)

        //봉사시간
        MyPageModel.volunteerHours.observeForever {
            _vmVolunteerHours.value = it
            calculateEx(it)
        }

        //봉사 횟수
        MyPageModel.volunteerCount.observeForever {
            _vmVolunteerCount.value = it
        }

        //리사이클러뷰 횟수
        MyPageModel.recyclerViewCount.observeForever {
            _vmRecyclerViewCount.value = it
        }

        //닉네임
        MyPageModel.ninkname.observeForever {
            _vmNickname.value = it
        }

        //봉사내역 리사이클러뷰 설정,업데이트
        MyPageModel.volunteerHistoryList.observeForever {
            _vmRecyclerViewCount.value = it.size
            vmVolunteerAdapter.setVolunteerList(it)
        }

        //리뷰 다이얼로그 질문 내용 리스트
        MyPageModel.dialogModels.observeForever {
            vmReviewDialog.value = it
        }

    }


    //봉사시간에 따라 레벨과 경험치 값 조정
    private fun calculateEx(hours: Int) {
        //봉사시간 10시간마다 레벨 업
        val level = hours / 10
        val progressValue = (hours % 10) * 10

        _vmLevel.value = level
        _progress.value = progressValue
        _progressPercent.value = progressValue
    }

    override fun onVolunteerClick(volunteer: Volunteer) {
        MyPageModel.getReviewQuestions()
    }



//=======

    /* 김동한
    * SignIn 로그인 Activity 에서 로그인을 하면, SharedPreferences에 jwtToken과 userInfo 객체를 담습니다.
    * 이제 SharedPreferences에서 꺼내서 userInfo(아바타) 에는 현재 <닉네임, 나이대, 경험(횟수), 레벨(경험치?)> 이 담겨져 있습니다.
     */

    private val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val _jwtToken = MutableLiveData<String>()
    val jwtToken: LiveData<String> get() = _jwtToken

    private val _userInfo = MutableLiveData<AvatarInfo>() // userInfo를 JSON 문자열로 가정
    val userInfo: LiveData<AvatarInfo> get() = _userInfo


}
