package com.example.potatoservice.ui.home

import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.potatoservice.R
import com.example.potatoservice.databinding.LoadingItemBinding
import com.example.potatoservice.databinding.ServiceItemBinding
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.ui.share.AdapterCallback
import kotlin.math.abs

class SearchResultAdapter(
	private val callback: AdapterCallback
) : ListAdapter<Activity, RecyclerView.ViewHolder>(

	object : DiffUtil.ItemCallback<Activity>() {
		override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
			return oldItem.actId == newItem.actId
		}

		override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
			return oldItem == newItem
		}
	}
) {
	inner class ViewHolder(
		private val binding: ServiceItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(activity: Activity) {
			binding.activity = activity
			// 카테고리에 따라 이미지 변경
			when (activity.category) {
				"생활지원 및 주거환경 개선" -> {
					binding.serviceImage.setImageResource(R.drawable.ic_categori_life)
				}
				"교육 및 멘토링" -> {
					binding.serviceImage.setImageResource(R.drawable.ic_categori_mentoring)
				}
				"행정 및 사무지원" -> {
					binding.serviceImage.setImageResource(R.drawable.ic_categori_office_support)
				}
				"문화, 환경 및 국제협력 활동" -> {
					binding.serviceImage.setImageResource(R.drawable.ic_categori_international)
				}
				"보건의료 및 공익활동" -> {
					binding.serviceImage.setImageResource(R.drawable.ic_categori_medical)
				}
				"상담 및 자원봉사 교육" -> {
					binding.serviceImage.setImageResource(R.drawable.ic_categori_volunteer_work)
				}
				"기타 활동" -> {
					binding.serviceImage.setImageResource(R.drawable.ic_categori_other)
				}
				else -> {
					// 기본 이미지 설정
					binding.serviceImage.setImageResource(R.drawable.ic_categori_other)
				}
			}
			binding.root.setOnClickListener {
				callback.onClicked(activity.actId)
			}
		}
	}

	inner class LoadingViewHolder(binding: LoadingItemBinding) : RecyclerView.ViewHolder(binding.root)


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			VIEW_TYPE_ITEM -> {
				val inflater = LayoutInflater.from(parent.context)
				val binding = ServiceItemBinding.inflate(inflater, parent, false)
				ViewHolder(binding) // ViewHolder 반환
			}
			VIEW_TYPE_LOADING -> {
				val inflater = LayoutInflater.from(parent.context)
				val binding = LoadingItemBinding.inflate(inflater, parent, false)
				LoadingViewHolder(binding) // LoadingViewHolder 반환
			}
			else -> throw IllegalArgumentException("Invalid view type")
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ViewHolder) {
			val activity = getItem(position)
			holder.bind(activity)
		} else {
			// 로딩 표시기 표시
		}
	}

	companion object {
		//뷰가 봉사 활동 아이템인지, 로딩 아이템인지 구분
		private const val VIEW_TYPE_ITEM = 0
		private const val VIEW_TYPE_LOADING = 1
	}

	// 로딩 상태
	private var isLoading = false
	//리사이클러뷰 위치 저장
	private var recyclerViewState: Parcelable? = null
	//서버에서 가져온 정보를 포함한 실제 아이템 개수
	private var nowItemCount = 0
	//직전 포지션
	private var previousPosition:Int? = null

	// 로딩 표시기 추가
	override fun getItemViewType(position: Int): Int {
		val activity = getItem(position)
		//마지막에 도달했고, 로딩 중이며 활동 ID가 -1인 경우(더미 데이터) 로딩
		return if (position == itemCount - 1 && isLoading && activity.actId == -1) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
	}

	fun setLoading(loading: Boolean) {
		isLoading = loading
		notifyItemChanged(itemCount - 1) // 로딩 표시기 가시성 업데이트
	}
	//현재 로딩한 것을 포함한 아이템 개수 설정
	fun setNowItemCount(count: Int) {
		nowItemCount = count
	}

	// 리사이클러뷰에 스크롤 리스너 연결
	fun attachToRecyclerView(recyclerView: RecyclerView) {
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val layoutManager = recyclerView.layoutManager as LinearLayoutManager
				//현재 포지션
				val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
				//맨 처음에는 이전 포지션을 현재 포지션으로 저장
				if (previousPosition == null){
					previousPosition = lastVisibleItemPosition
				}
				val positionGap = abs(previousPosition!! - lastVisibleItemPosition)

				//포지션 갭이 1보다 큰 경우 오류 현상임.
				if (positionGap>1){
					layoutManager.scrollToPosition(0)
				}
				else{
					previousPosition = lastVisibleItemPosition
				}
				//지금 보고 있는 아이템 포지션이 맨 마지막이고, 로딩 중이 아니면서 아이템 개수가 실제 아이템 개수보다 같거나 크고, 이전 포지션과 현재 포지션이 이어지는 경우
				if (lastVisibleItemPosition == itemCount - 1 && !isLoading && itemCount >= nowItemCount && positionGap <= 1) {
					//현재 위치 저장
					recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
					// 더 많은 아이템 로드
					setLoading(true)
					callback.loadMoreActivities(recyclerViewState) // ViewModel에 더 로드하도록 요청

				}
			}
		})
	}


	//봉사 활동 정보 업데이트
	fun submitListWithSetLoading(list: List<Activity>) {
		super.submitList(list)
		setLoading(false)
	}
}

