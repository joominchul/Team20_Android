package com.example.potatoservice.ui.mypage

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.potatoservice.R
import com.example.potatoservice.model.remote.ReviewAnswer
import com.example.potatoservice.model.remote.ReviewRequest
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

    private val _vmReviewDialog = MutableLiveData<Event<List<DialogModel>>>()
    val vmReviewDialog: LiveData<Event<List<DialogModel>>> = _vmReviewDialog

    //jwt토큰과, 유저정보
    private val sharedPref = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val _jwtToken = MutableLiveData<String>()
//    val jwtToken: LiveData<String> get() = _jwtToken


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

        //레벨
        MyPageModel.level.observeForever {
            _vmLevel.value = it
        }

        //봉사내역 리사이클러뷰 설정,업데이트
        MyPageModel.volunteerHistoryList.observeForever {
            _vmRecyclerViewCount.value = it.size
            vmVolunteerAdapter.setVolunteerList(it)
        }

        //리뷰 다이얼로그 질문 내용 리스트
        MyPageModel.dialogModels.observeForever {
            _vmReviewDialog.value = Event(it)
        }

        _jwtToken.value = sharedPref.getString("jwt_token", null)

    }


    //봉사시간에 따라 레벨과 경험치 값 조정
    private fun calculateEx(hours: Int) {
        //봉사시간 10시간마다 레벨 업
        val level = hours / 10
        val progressValue = (hours % 10) * 10

//        _vmLevel.value = level
        _progress.value = progressValue
        _progressPercent.value = progressValue
    }

    fun onRefreshClick() {
        MyPageModel.getMyPageList(_jwtToken.value.toString())
    }


    override fun checkReview(volunteer: Volunteer) {
        MyPageModel.setReviewHistoryId(volunteer.id)
        MyPageModel.getReviewQuestions()
    }

    //MyPageViewModel
    fun sendReview(id: Int, ratingData: Map<Int, Float>) {

        val filteredData = ratingData.filterKeys { it != 0 }
        val reviewData = filteredData.mapKeys { it.key - 1 }

        Log.d("seyoung","review id : $id")
        Log.d("seyoung","review ratingData : $reviewData")

        val answers = reviewData.map { entry ->
            ReviewAnswer(questionId = entry.key, score = entry.value)
        }

        Log.d("seyoung", "$answers.toString()")


        val requestBody = ReviewRequest(
            historyId = id,
            answers = answers
        )

        // API 호출
        MyPageModel.sendReview(_jwtToken.value!!, requestBody)
    }


//    override fun onVolunteerClick(volunteer: Volunteer) {
//        //todo 디테일로 인텐트하기
//        val intent = Intent(requireContext(), DetailActivity::class.java)
//        intent.putExtra("id", id) // 데이터 추가
//        startActivity(intent)
//    }
}

class Event<T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}
