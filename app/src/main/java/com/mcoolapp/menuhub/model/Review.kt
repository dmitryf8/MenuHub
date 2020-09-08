package com.mcoolapp.menuhub.model

import java.util.*

class Review {
    private lateinit var id: String
    private lateinit var postID: String
    private lateinit var postOwnerID: String
    private lateinit var reviewAuthorID: String
    private lateinit var dateTime: Date

    private lateinit var likedIDList: List<String>
    private lateinit var commentList: List<Comment>
}