package com.example.potatoservice.ui.home

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.potatoservice.databinding.FragmentHomeBinding
import com.example.potatoservice.ui.detail.DetailActivity
import com.example.potatoservice.ui.share.AdapterCallback
import com.example.potatoservice.ui.share.Request
import com.example.potatoservice.ui.share.SpinnerHintAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), AdapterCallback {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var searchResultAdapter: SearchResultAdapter
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = homeViewModel
        setRecyclerAdapter()
        setSpinner()
        showLoading()
        //검색 버튼 클릭 시
        binding.searchButton.setOnClickListener {
            val page = 0
            val size: Int? = null
            val sort: String? = null
            val beforeDeadlineOnly: Boolean? = null
            val teenPossibleOnly: Boolean? = null
            val category: String? = null
            val request = Request(page, size, sort, beforeDeadlineOnly, teenPossibleOnly, category)
            homeViewModel.search(request)
        }
        return binding.root
    }

    //로딩 화면 설정
    private fun showLoading() {
        homeViewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            if (loading) {
                binding.searchResultRecyclerView.visibility = View.GONE
                binding.loadingShimmer.visibility = View.VISIBLE
                binding.loadingShimmer.startShimmer()
            } else {
                binding.loadingShimmer.stopShimmer()
                binding.loadingShimmer.visibility = View.GONE
                binding.searchResultRecyclerView.visibility = View.VISIBLE
            }
        })

    }

    //검색 결과 리사이클러뷰 설정
    private fun setRecyclerAdapter() {
        binding.searchResultRecyclerView.layoutManager = LinearLayoutManager(activity)
        searchResultAdapter = SearchResultAdapter(this)
        homeViewModel.activityList.observe(viewLifecycleOwner, Observer { service ->
            searchResultAdapter.submitList(service)
            binding.searchResultRecyclerView.adapter = searchResultAdapter
        })
    }

    //필터들 설정
    private fun setSpinner() {
        // 정렬 스피너 설정
        val sortAdapter = ArrayAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.sortList
        )
        sortAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
        binding.sort.adapter = sortAdapter
        //정렬 선택 시
        binding.sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // 지역 대분류 스피너 설정
        val majorRegionAdapter = SpinnerHintAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.majorRegoinList
        )
        majorRegionAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
        binding.majorRegionalCategories.adapter = majorRegionAdapter
        //지역 대분류 선택 시
        binding.majorRegionalCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    //지역 대분류 선택에 따라 소분류 목록이 바뀜
                    val minorRegionAdapter =SpinnerHintAdapter(
                        requireContext(),
                        com.example.potatoservice.R.layout.spinner_item,
                        homeViewModel.minorRegoinList[position]
                    )
                    minorRegionAdapter.setDropDownViewResource(
                        com.example.potatoservice.R.layout.spinner_item_dropdown)
                    binding.minorRegionalCategories.adapter = minorRegionAdapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }

        //지역 소분류 스피너 설정
        val minorRegionAdapter =SpinnerHintAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.minorRegoinList[0]
        )
        minorRegionAdapter.setDropDownViewResource(
            com.example.potatoservice.R.layout.spinner_item_dropdown)
        binding.minorRegionalCategories.adapter = minorRegionAdapter
        //지역 소분류 선택 시
        binding.minorRegionalCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }


        // 봉사 분야 스피너 설정
        val volunteerActivitiesAdapter = SpinnerHintAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.volunteerList
        )
        volunteerActivitiesAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
        binding.volunteerActivitiesCategories.adapter = volunteerActivitiesAdapter
        //봉사 분야 선택 시
        binding.volunteerActivitiesCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }


        // 나이 제한 스피너 설정
        val ageAdapter = SpinnerHintAdapter(
            requireContext(),
            com.example.potatoservice.R.layout.spinner_item,
            homeViewModel.ageList
        )
        ageAdapter.setDropDownViewResource(com.example.potatoservice.R.layout.spinner_item_dropdown) // 드롭다운 항목 레이아웃 설정
        binding.ageCategories.adapter = ageAdapter
        //나이 제한 선택 시
        binding.ageCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    override fun onClicked(id: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("id", id) // 데이터 추가
        startActivity(intent)
    }


}