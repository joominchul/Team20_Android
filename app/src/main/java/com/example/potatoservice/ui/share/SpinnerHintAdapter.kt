package com.example.potatoservice.ui.share

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerHintAdapter(context: Context, resource: Int, objects: List<String>?) :
    ArrayAdapter<String>(context, resource, objects!!) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view as TextView
        // 재활용된 뷰인 경우 스타일 초기화
        if (convertView != null) {
            textView.setTextColor(Color.BLACK)
            textView.setTypeface(null, Typeface.NORMAL) // 볼드 처리 해제
        }
        if (position == 0) {
            // 첫 번째 항목 스타일 변경
            textView.setTextColor(Color.LTGRAY)
            textView.setTypeface(textView.typeface, Typeface.BOLD)

        } else {
            // 나머지 항목 스타일 설정
            textView.setTextColor(Color.BLACK)
            textView.setTypeface(textView.typeface, Typeface.NORMAL)
        }

        return view
    }

}