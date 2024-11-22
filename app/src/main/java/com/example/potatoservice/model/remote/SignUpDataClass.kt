package com.example.potatoservice.model.remote

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/* 로그인 요청
* 유저 정보를 반환
 */
data class LoginRequest(
//    val avatar: Map<String, Double>? // avatar를 Map 형태로 받도록 수정
    val avatar: AvatarInfo? // avatar가 null일 수 있으므로 nullable로 설정
)

/* 유저 정보
* 유저 정보의 <객체> 닉네임, 나이대, 경험, 레벨 등
 */
//data class AvatarInfo(
//    val avatarId: Int,  //개인 아바타 id
//    val avatarExp: Int, //경험치
//    val avatarLevel: Int,   //레벨
//    val nickName: String,   //닉네임
//    val ageRange: String,   //나이대
//    val experienced: String //봉사 경험정도
//) : Serializable

data class AvatarInfo(
    @SerializedName("avatarId")
    val avatarId: Int,

    @SerializedName("avatarExp")
    val avatarExp: Int,

    @SerializedName("avatarLevel")
    val avatarLevel: Int,

    @SerializedName("nickName")
    val nickName: String,

    @SerializedName("ageRange")
    val ageRange: String,

    @SerializedName("experienced")
    val experienced: String
) : Serializable

/* 회원 가입 요청
* 회원 가입 요청 <객체>
 */
data class SendSignUpUserInfo(
    val nickname: String,
    val ageRange: String,
    val experienced: String
)