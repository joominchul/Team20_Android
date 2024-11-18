package com.example.potatoservice.ui.mypage

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.potatoservice.databinding.DialogCustomBinding

class CustomDialogFragment : DialogFragment() {

    private lateinit var binding: DialogCustomBinding

    // 다이얼로그에서 발생한 클릭 이벤트를 전달할 인터페이스
    interface OnDialogButtonClickListener {
        fun onDialogCompleted(historyId : Int, ratingData: Map<Int, Float>)
    }

    private var listener: OnDialogButtonClickListener? = null
    private var dialogList: List<DialogModel> = listOf()
    private var dialogIndex: Int = 0
    private val ratingData = mutableMapOf<Int, Float>()  // 각 다이얼로그의 별점 저장
    private var historyId : Int = 0

    companion object {
        // 인스턴스 생성 시, 첫 번째 질문과 dialogList를 전달받아 초기화
        fun newInstance(historiesId : Int, dialogList: List<DialogModel>): CustomDialogFragment {
            val fragment = CustomDialogFragment()
            fragment.historyId = historiesId
            // 첫 번째 고정 질문 추가
            fragment.dialogList = listOf(DialogModel(
                "봉사 수행 여부", "봉사를 수행하셨습니까?",
                "아니요", "네")) + dialogList
            fragment.dialogIndex = 0
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCustomBinding.inflate(inflater, container, false)


        // RatingBar 최소값을 0.5로 설정하는 리스너
        binding.customDialogRating.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating < 0.5f) {
                binding.customDialogRating.rating = 0.5f  // 최소 값으로 강제 설정
            }
        }

        binding.customDialogClose.setOnClickListener { dismiss() }

        binding.customDialogPrevious.setOnClickListener {
            if (dialogIndex > 0) {
                saveCurrentRating()
                dialogIndex--
                updateDialogUI(dialogList[dialogIndex])
            }
        }

        binding.customDialogNext.setOnClickListener {
            saveCurrentRating()
            if (dialogIndex < dialogList.size - 1) {
                dialogIndex++
                updateDialogUI(dialogList[dialogIndex])
            } else {
                saveCurrentRating()
                listener?.onDialogCompleted(historyId,ratingData)
                dismiss()
            }
        }

        // 이전 버튼 클릭 리스너
        binding.customDialogPrevious.setOnClickListener {
            if (dialogIndex == 0) {
                // 첫 번째 페이지일 때는 다이얼로그 종료
                dismiss()
            } else if (dialogIndex == 1) {
                // 두 번째 페이지일 때는 이전 버튼을 비활성화
                binding.customDialogPrevious.isEnabled = false
                dialogIndex--
                updateDialogUI(dialogList[dialogIndex])
            } else {
                // 두 번째 이후 페이지부터는 이전 페이지로 이동
                dialogIndex--
                updateDialogUI(dialogList[dialogIndex])
            }
        }

        updateDialogUI(dialogList[dialogIndex])

        return binding.root
    }


    // 현재 별점 저장
    private fun saveCurrentRating() {
        ratingData[dialogIndex] = binding.customDialogRating.rating
    }

    // 다이얼로그 UI 업데이트
    // 다이얼로그 UI 업데이트
    private fun updateDialogUI(dialogModel: DialogModel) {
        binding.customDialogTitle.text = dialogModel.title
        binding.customDialogContent.text = dialogModel.content

        // 이전 버튼 활성화 여부 설정
        binding.customDialogPrevious.isEnabled = (dialogIndex != 1)

        // 0번째 다이얼로그에서는 RatingBar 숨기기, 그 외 다이얼로그에서는 보이기
        binding.customDialogRating.visibility = if (dialogIndex == 0) View.GONE else View.VISIBLE

        // 첫 번째 다이얼로그일 경우 "아니요"와 "네"로 버튼 텍스트 설정
        if (dialogIndex == 0) {
            binding.customDialogPrevious.text = dialogModel.previousButtonText
            binding.customDialogNext.text = dialogModel.nextButtonText
        } else {
            // 첫 번째가 아닌 경우 "이전"과 "다음"(마지막은 "저장")으로 설정
            binding.customDialogPrevious.text = "이전"
            binding.customDialogNext.text = if (dialogIndex == dialogList.size - 1) "저장" else "다음"
        }

        binding.customDialogRating.rating = ratingData[dialogIndex] ?: 0.5f
    }


    // 다이얼로그 크기 설정
    override fun onStart() {
        super.onStart()
        val displayMetrics = Resources.getSystem().displayMetrics
        val dialogWidth = (displayMetrics.widthPixels * 0.8).toInt()  // 너비 80%
        val dialogHeight = (displayMetrics.heightPixels * 0.5).toInt() // 높이 50%

        dialog?.window?.setLayout(dialogWidth, dialogHeight)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    // 리스너 설정 메서드
    fun setDialogListener(listener: OnDialogButtonClickListener) {
        this.listener = listener
    }
}

