package com.mcoolapp.menuhub.model.user

data class UserCommunicationData(
    val id: String,
    var userReviewIDList: List<String>,//список отзывов пользователя
    var userCommentIDList: List<String>//список комментариев пользователя
 )