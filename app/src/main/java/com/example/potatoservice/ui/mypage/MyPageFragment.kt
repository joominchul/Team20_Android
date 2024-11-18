package com.example.potatoservice.ui.mypage

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.potatoservice.MainViewModel
import com.example.potatoservice.R
import com.example.potatoservice.databinding.FragmentMypageBinding
import com.example.potatoservice.ui.share.Volunteer

class MyPageFragment : Fragment(), CustomDialogFragment.OnDialogButtonClickListener {

    private lateinit var binding: FragmentMypageBinding
    private lateinit var myPageViewModel: MyPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = MyPageViewModelFactory(requireContext())
        myPageViewModel = ViewModelProvider(this, factory).get(MyPageViewModel::class.java)
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        //todo 스피너 수정
//        binding.myPageSpinner.adapter = myPageViewModel.vmSpinnerAdapter

        observeDialogModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInit()
        binding.mypageRefresh.setOnClickListener {
            //개인 히스토리 새로고침
            myPageViewModel.onRefreshClick()
        }


    }

    private fun setUpInit() {
        setupProgressBar()
        setupRecyclerView()
        setupTvLevel()
        setupTvTotalHours()
//        setupTvTotalCount()
        setupRecyclerViewCount()
        setupNickname()
        setupLvimage()
        setuplvtitle()
    }

    //nickname 설정 함수
    private fun setupNickname() {
        myPageViewModel.vmNickname.observe(viewLifecycleOwner) {
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
        myPageViewModel.progressPercent.observe(viewLifecycleOwner) {
            binding.tvProgressPercent.text = "${it}%"
        }

    }

    //총 봉사 건수 설정
//    private fun setupTvTotalCount(){
//        myPageViewModel.vmVolunteerCount.observe(viewLifecycleOwner, Observer {
//            binding.tvTotalVolunteerCount.text = "총 봉사 건수 : ${it} 건"
//        })
//    }


    //총 봉사 시간 설정
    private fun setupTvTotalHours() {
        myPageViewModel.vmVolunteerHours.observe(viewLifecycleOwner, Observer {
            binding.tvTotalHours.text = "총 봉사 시간 : ${it}"
        })
    }

    // 레벨 설정
    private fun setupTvLevel() {
        myPageViewModel.vmLevel.observe(viewLifecycleOwner, Observer {
            binding.tvLevel.text = "Lv. ${it}"
        })
    }

    private fun setupLvimage() {
        myPageViewModel.vmLevel.observe(viewLifecycleOwner, Observer {
            if (it < 10) {
                binding.lvImage.setImageResource(R.drawable.potato_lv1)
            } else {
                when (it / 10) {
                    1 -> binding.lvImage.setImageResource(R.drawable.potato_lv10)
                    2 -> binding.lvImage.setImageResource(R.drawable.potato_lv20)
                    3 -> binding.lvImage.setImageResource(R.drawable.potato_lv30)
                    4 -> binding.lvImage.setImageResource(R.drawable.potato_lv40)
                    5 -> binding.lvImage.setImageResource(R.drawable.potato_lv50)
                }
            }
        })
    }

    private fun setuplvtitle() {
        myPageViewModel.vmLevel.observe(viewLifecycleOwner, Observer {
            if (it < 10) {
                binding.lvTitle.text = "감자 새싹"
            }
            when (it / 10) {
                1 -> binding.lvTitle.text = "알 감자"
                2 -> binding.lvTitle.text = "감자 바구니"
                3 -> binding.lvTitle.text = "감자 박스"
                4 -> binding.lvTitle.text = "감자 컨테이너"
                5 -> binding.lvTitle.text = "감자 화물선"
            }
        })
    }

    //봉사히스토리 카운트 설정
    private fun setupRecyclerViewCount() {
        myPageViewModel.vmRecyclerViewCount.observe(viewLifecycleOwner, Observer {
            binding.mypageRecyclerViewCount.text = "총 ${it}건"
        })
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        // 어댑터 설정(서버로부터 받아오는)
        binding.recyclerView.adapter = myPageViewModel.vmVolunteerAdapter //이게 진짜
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    // 다이얼로그 모델 관찰
    private fun observeDialogModel() {
        myPageViewModel.vmReviewDialog.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { dialogModel ->
                Log.d("seyoung", "observeDialogModel: $dialogModel")
                val customDialog = CustomDialogFragment.newInstance(
                    MyPageModel.reviewHistoryId.value ?: 0,
                    dialogModel
                )
                customDialog.setDialogListener(this)
                customDialog.show(parentFragmentManager, "customDialog")
            }
        }
    }

    override fun onDialogCompleted(historyId: Int, ratingData: Map<Int, Float>) {
        myPageViewModel.sendReview(historyId, ratingData)

    }

}
