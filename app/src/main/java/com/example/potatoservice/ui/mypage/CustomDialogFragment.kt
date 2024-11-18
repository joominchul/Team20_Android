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
        fun onDialogCompleted(ratingData: Map<Int, Float>)
    }

    private var listener: OnDialogButtonClickListener? = null
    private var dialogList: List<DialogModel> = listOf()
    private var dialogIndex: Int = 0
    private val ratingData = mutableMapOf<Int, Float>()  // 각 다이얼로그의 별점 저장

    companion object {
        // 인스턴스 생성 시, dialogList를 전달받아 초기화
        fun newInstance(dialogList: List<DialogModel>): CustomDialogFragment {
            val fragment = CustomDialogFragment()
            fragment.dialogList = dialogList
            fragment.dialogIndex = 0
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCustomBinding.inflate(inflater, container, false)

        updateDialogUI(dialogList[dialogIndex])

        // 닫기 버튼 이벤트 처리
        binding.customDialogClose.setOnClickListener {
            dismiss()  // 별점 저장 없이 종료
        }

        // 이전 버튼 클릭 처리
        binding.customDialogPrevious.setOnClickListener {
            if (dialogIndex > 0) {
                saveCurrentRating()  // 현재 별점 저장
                dialogIndex--
                updateDialogUI(dialogList[dialogIndex])
            }
        }

        // 다음 버튼 클릭 처리
        binding.customDialogNext.setOnClickListener {
            saveCurrentRating()  // 현재 별점 저장
            if (dialogIndex < dialogList.size - 1) {
                dialogIndex++
                updateDialogUI(dialogList[dialogIndex])
            } else {
                // 마지막 다이얼로그인 경우 저장 여부 확인
                showSaveConfirmation()
            }
        }

        return binding.root
    }

    // 현재 별점 저장
    private fun saveCurrentRating() {
        ratingData[dialogIndex] = binding.customDialogRating.rating
    }

    // 다이얼로그 UI 업데이트
    private fun updateDialogUI(dialogModel: DialogModel) {
        binding.customDialogTitle.text = dialogModel.title
        binding.customDialogContent.text = dialogModel.content
        binding.customDialogPrevious.isEnabled = (dialogIndex > 0)
        binding.customDialogNext.text = if (dialogIndex == dialogList.size - 1) "저장" else "다음"
        binding.customDialogRating.rating = ratingData[dialogIndex] ?: 0f
    }

    // 저장 여부를 묻는 확인 다이얼로그
    private fun showSaveConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("저장 확인")
            .setMessage("평가를 저장하시겠습니까?")
            .setPositiveButton("저장") { _, _ ->
                listener?.onDialogCompleted(ratingData)  // 저장 후 완료 리스너 호출
                dismiss()
            }
            .setNegativeButton("취소") { dialogInterface: DialogInterface, _ ->
                dialogInterface.dismiss()
                dismiss()  // 취소 시 그냥 다이얼로그 종료
            }
            .show()
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
