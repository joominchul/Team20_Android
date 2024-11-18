package com.example.potatoservice.model.remote

import com.example.potatoservice.ui.share.SpinnerList.kangwonUnivLatitude
import com.example.potatoservice.ui.share.SpinnerList.kangwonUnivLongitude

data class Activity(
    val actId: Int,
    val actTitle: String?,
    val actLocation: String?,
    val noticeStartDate: String?,
    val noticeEndDate: String,
    val actStartDate: String?,
    val actEndDate: String,
    val actStartTime: Int,
    val actEndTime: Int,
    val recruitTotalNum: Int,
    val category: String
)

data class ActivityResponse(
    val content: List<Activity>,
    val pageable: Pageable,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val empty: Boolean
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: Sort,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

data class ActivityDetail(
    val actId: Int,
    val actTitle: String?,
    val actLocation: String?,
    val description: String?,
    val noticeStartDate: String?,
    val noticeEndDate: String,
    val actStartDate: String?,
    val actEndDate: String,
    val actStartTime: Int,
    val actEndTime: Int,
    val recruitTotalNum: Int,
    val adultPossible: Boolean,
    val teenPossible: Boolean,
    val groupPossible: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val actWeek: Int,
    val actManager: String,
    val actPhone: String,
    val url: String,
    val category: String,
    val institute: Institute
){
    companion object{
        val nullActivityDetail = ActivityDetail(
            0,
            "봉사 활동 제목 정보 없음",
            "봉사 활동 장소 정보 없음",
            "봉사 활동 설명 정보 없음",
            "공지 시작 날짜 정보 없음",
            "공지 종료 날짜 정보 없음",
            "봉사 활동 시작 날짜 정보 없음",
            "봉사 활동 종료 날짜 정보 없음",
            0,
            0,
            0,
            false,
            false,
            false,
            kangwonUnivLatitude,
            kangwonUnivLongitude,
            0,
            "봉사 활동 담당자 정보 없음",
            "봉사 활동 전화 번호 정보 없음",
            "봉사 활동 url 정보 없음",
            "카테고리 정보 없음",
            Institute(
                0,
                "기관 정보 없음",
                "기관 주소 정보 없음",
                //강원대 위치
                kangwonUnivLatitude,
                kangwonUnivLongitude,
                "기관 전화 번호 정보 없음",
                listOf(
                    Score(
                        Question(
                            0,
                            "질문 정보 없음"
                        ),
                        -1.0
                    )
                )
            )
        )
        val nullActivity = Activity(
            -1,
        null,
        null,
        null,
        "noticeEndDate",
        null,
        "actEndDate",
        -1,
        -1,
        -1,
        "category"
        )
    }
}
//기관 정보
data class Institute(
    val instituteId:Int,
    val name:String,
    val location:String,
    val latitude:Double?,
    val longitude:Double?,
    val phone:String,
    val scores:List<Score>?
)
//기관 리뷰 정보
data class Score(
    val question:Question,
    val score:Double
)
//리뷰 질문 정보
data class Question(
    val questionId:Int,
    val content:String
)

//시도군구 데이터 클래스
data class SidoGungu(
    val sidoGunguCode:Int,
    val sidoCode: Int,
    val sidoName: String,
    val gunguName: String?,
    val sido: Boolean
)

data class VolunteerHistoryResponse(
    val content: List<HistoryItem>
)

// 봉사 내역 항목
data class HistoryItem(
    val historyId: Int,
    val avatarId: Int,
    val activity: Activity,
    val activityStatus: String,
    val reviewed: Boolean
)

//리뷰 질문 정보
data class Review(
    val reviewId: Int,
    val content: String
)

//리뷰 저장 전송 정보
data class ReviewRequest(
    val historyId: Int,
    val answers: List<ReviewAnswer>
)

//리뷰 저장
data class ReviewAnswer(
    val questionId: Int,
    val score: Float
)


// 카카오 맵 API DATA CLASS
data class AddressResponse(
    val meta: Meta,
    val documents: List<Document>
)

data class Meta(
    val total_count: Int,
    val pageable_count: Int,
    val is_end: Boolean
)

data class Document(
    val address_name: String,
    val x: String,  // 경도 (longitude)
    val y: String,  // 위도 (latitude)
    val address: Address?,
    val road_address: RoadAddress?
)

data class Address(
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val x: String,
    val y: String
)

data class RoadAddress(
    val address_name: String,
    val road_name: String,
    val main_building_no: String,
    val sub_building_no: String,
    val x: String,
    val y: String
)



