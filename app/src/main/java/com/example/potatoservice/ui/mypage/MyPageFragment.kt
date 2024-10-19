package com.example.potatoservice.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.potatoservice.databinding.FragmentMypageBinding
import com.example.potatoservice.ui.share.Volunteer

class MyPageFragment : Fragment(), OnVolunteerClickListener, CustomDialogFragment.OnDialogButtonClickListener{

    private lateinit var binding: FragmentMypageBinding
    private lateinit var myPageViewModel: MyPageViewModel
    private lateinit var customDialog : CustomDialogFragment
    private lateinit var dialogArray : Array<DialogModel>

    var dialogShowCount = 0
    var positiveCount = 0
    var negativeCount = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myPageModel = MyPageModel(requireContext())
        val factory = MyPageViewModelFactory(requireContext(), myPageModel)
        myPageViewModel = ViewModelProvider(this, factory).get(MyPageViewModel::class.java)
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        binding.myPageSpinner.adapter = myPageViewModel.vmSpinnerAdapter
        dialogArray = myPageViewModel.vmDialogArray

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPageViewModel.setVolunteerHours()
        myPageViewModel.setVolunteerCount()
        myPageViewModel.setRecyclerViewCount()
        setUpInit()
    }

    private fun setUpInit(){
        setupProgressBar()
        setupRecyclerView()
        setupTvLevel()
        setupTvTotalHours()
        setupTvTotalCount()
        setupRecyclerViewCount()
    }

    // ProgressBar 설정 함수
    private fun setupProgressBar() {
        // ViewModel의 progress 값을 관찰하고 ProgressBar에 반영
        myPageViewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progressBar.progress = progress
        }
        myPageViewModel.progressPercent.observe(viewLifecycleOwner){
            binding.tvProgressPercent.text = "${it}%"
        }
    }

    //총 봉사 건수 설정
    private fun setupTvTotalCount(){
        myPageViewModel.vmVolunteerCount.observe(viewLifecycleOwner, Observer {
            binding.tvTotalVolunteerCount.text = "총 봉사 건수 : ${it} 건"
        })
    }

    //총 봉사 시간 설정
    private fun setupTvTotalHours(){
        myPageViewModel.vmVolunteerHours.observe(viewLifecycleOwner, Observer {
            binding.tvTotalHours.text = "총 봉사 시간 : ${it}"
        })
    }

    // 레벨 설정
    private fun setupTvLevel(){
        myPageViewModel.vmLevel.observe(viewLifecycleOwner, Observer {
            binding.tvLevel.text = "Lv. ${it}"
        })
    }

    private fun setupRecyclerViewCount(){
        myPageViewModel.vmRecyclerViewCount.observe(viewLifecycleOwner, Observer {
            binding.mypageRecyclerViewCount.text = "총 ${it}건"
        })
    }



    // RecyclerView 설정 함수
    private fun setupRecyclerView() {

        // 예시 데이터 리스트 생성
        //todo mvvm패턴 변경하기
        val volunteers = listOf(
            Volunteer("테스트id","봉사활동 1", "기관 A", "교육", "2024.09.01 ~ 2024.09.30", "0/5", "2024.10.01 ~ 2024.10.31", "132시간", "서울특별시", "확정 대기 중"),
            Volunteer("테스트id","봉사활동 2", "기관 B", "환경", "2024.08.01 ~ 2024.08.30", "3/10", "2024.09.01 ~ 2024.09.15", "32시간", "부산광역시", "신청 완료됨"),
            Volunteer("테스트id","봉사활동 3", "기관 B", "환경", "2024.08.01 ~ 2024.08.30", "3/10", "2024.09.01 ~ 2024.09.15", "32시간", "부산광역시", "신청 완료됨"),
            Volunteer("테스트id","봉사활동 4", "기관 B", "환경", "2024.08.01 ~ 2024.08.30", "3/10", "2024.09.01 ~ 2024.09.15", "32시간", "부산광역시", "신청 완료됨")
            // 더 많은 데이터 추가 가능
        )

        // 어댑터 설정
        val adapter = VolunteerAdapter(volunteers,this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onVolunteerClick(volunteer: Volunteer) {

//        val customDialog = CustomDialogFragment.newInstance(
//            title = "테스트제목",
//            image = R.drawable.potato_lv1,
//            content = "1긴테스트내용2긴테스트내용3긴테스트내용4긴테스트내용5긴테스트내용6긴테스트내용7긴테스트내/용8긴테스트내용9긴테스트내용"
//        ) //MAX 24sp 기준 띄어쓰기 포함 50글자
//            //TODO content 50글자 넘을 시 처리

        dialogShowCount = 0
        positiveCount = 0
        negativeCount = 0
        showDialog()
    }

    fun showDialog(){

        if(dialogShowCount<5){
            customDialog = CustomDialogFragment.newInstance(
                dialogArray[dialogShowCount]
            )
            customDialog.setDialogListener(this@MyPageFragment)
            customDialog.show(parentFragmentManager,"customDialog")
            dialogShowCount++
        }
        else{
            Log.d("seyoung","positive : ${positiveCount}, negative : ${negativeCount}")
        }
    }

    override fun onPositiveButtonClick() {
        positiveCount++ // 긍정 버튼 클릭 카운트 증가
        showDialog() // 다음 다이얼로그 호출
    }

    override fun onNegativeButtonClick() {
        negativeCount++ // 부정 버튼 클릭 카운트 증가
        showDialog() // 다음 다이얼로그 호출
    }
}
