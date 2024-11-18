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



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = MyPageViewModelFactory(requireContext())
        myPageViewModel = ViewModelProvider(this, factory).get(MyPageViewModel::class.java)
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        binding.myPageSpinner.adapter = myPageViewModel.vmSpinnerAdapter

        observeDialogModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        myPageViewModel.jwtToken.observe(viewLifecycleOwner) { jwtToken -> Log.d("testt", "MyPage JWT Token: $jwtToken") }
//        myPageViewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
//            Log.d("testt", "MyPage User Info: $userInfo")
//        }
        setUpInit()
    }

    private fun setUpInit(){
        setupProgressBar()
        setupRecyclerView()
        setupTvLevel()
        setupTvTotalHours()
        setupTvTotalCount()
        setupRecyclerViewCount()
        setupNickname()
    }

    //nickname 설정 함수
    private fun setupNickname(){
        myPageViewModel.vmNickname.observe(viewLifecycleOwner){
            binding.tvNickname.text = it
        }
    }

    // ProgressBar 설정 함수
    private fun setupProgressBar() {
        // ViewModel의 progress 값을 관찰하고 ProgressBar에 반영
        myPageViewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progressBar.progress = progress
        }
        //progress값에 해당하는 text설정
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

    //봉사히스토리 카운트 설정
    private fun setupRecyclerViewCount(){
        myPageViewModel.vmRecyclerViewCount.observe(viewLifecycleOwner, Observer {
            binding.mypageRecyclerViewCount.text = "총 ${it}건"
        })
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        // 예시 데이터 리스트 생성
        val exVolunteerList = listOf(
            Volunteer(1,"서버로부터 못 받아온거임", "기관 A", "교육",
                "2024.09.01 ~ 2024.09.30", 5,
                "2024.10.01 ~ 2024.10.31", "132시간", "서울특별시", "확정 대기 중")
        )
        // 어댑터 설정(서버로부터 받아오는)
        binding.recyclerView.adapter = myPageViewModel.vmVolunteerAdapter //이게 진짜
//        binding.recyclerView.adapter = VolunteerAdapter(exVolunteerList,this) //임시로 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    // 다이얼로그 모델 관찰
    private fun observeDialogModel() {
        myPageViewModel.vmReviewDialog.observe(viewLifecycleOwner) { dialogModel ->
            Log.d("seyoung", "observeDialogModel: $dialogModel")
            val customDialog = CustomDialogFragment.newInstance(dialogModel)
            customDialog.setDialogListener(this)
            customDialog.show(parentFragmentManager, "customDialog")
        }
    }

    override fun onVolunteerClick(volunteer: Volunteer) {
        MyPageModel.getReviewQuestions()
    }

    override fun onDialogCompleted(ratingData: Map<Int, Float>) {
//        myPageViewModel에 값 넣기
    }


}
