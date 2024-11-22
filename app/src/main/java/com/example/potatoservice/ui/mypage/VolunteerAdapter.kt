package com.example.potatoservice.ui.mypage

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.potatoservice.databinding.ItemVolunteerBinding
import com.example.potatoservice.ui.share.Volunteer

class VolunteerAdapter(
    private var volunteerList: List<Volunteer>,
    private val listener: OnVolunteerClickListener
) : RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    inner class VolunteerViewHolder(val binding: ItemVolunteerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(volunteer: Volunteer) {
            val itemId = volunteer.id
            binding.tvVolunteerTitle.text = volunteer.title
            binding.tvInstitutionName.text = volunteer.institution
            binding.tvVolunteerCategory.text = volunteer.Category
            binding.tvRecruitmentPeriod.text = "모집 기간 : " + volunteer.recruitmentPeriod
            binding.tvRecruitmentCount.text = volunteer.recruitmentCount.toString()
            binding.tvActivityPeriod.text = "활동 기간 : " + volunteer.activityPeriod
            binding.tvVolunteerHours.text = volunteer.volunteerHours
            binding.tvVolunteerAddress.text = "활동 주소 : " + volunteer.address
            binding.tvVolunteerStatus.text = volunteer.status.let{
                when(it){
                    "APPLIED" -> "모집중"
                    "WAITING" -> "활동대기"
                    "ACT" -> "활동"
                    "FINISHED" -> "활동완료"
                    "REVIEWED" -> "리뷰완료"
                    else -> "기본"
                }
            }
            binding.btnAction.setOnClickListener {
                if(volunteer.status == "FINISHED"){
                    listener.checkReview(volunteer)
                }
                else{
                    //todo 디테일로 이동하기?
//                    listener.onVolunteerClick(volunteer)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val binding = ItemVolunteerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolunteerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        holder.bind(volunteerList[position])
    }

    override fun getItemCount(): Int {
        return volunteerList.size
    }

    fun setVolunteerList(newList: List<Volunteer>) {
        volunteerList = newList
        notifyDataSetChanged()
    }


}

