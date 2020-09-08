package com.mcoolapp.menuhub.model.chat

import com.google.firebase.firestore.DocumentId

data class DocumentIdAndText(
    val documentId: String,
    val text: String
) {
}