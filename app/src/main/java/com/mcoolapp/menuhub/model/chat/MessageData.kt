package com.mcoolapp.menuhub.model.chat

import com.google.firebase.Timestamp
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import java.util.*

class MessageData {
    var messageID: String = ""
    var messageType: String = ""
    var messageContent: String = ""
    var messageStatus: String = ""
    var imageWithBucket: ImageWithBucket = ImageWithBucket("", "")
    var time = ""
    var description: String = ""
    var senderID: String = ""
    var senderName: String = ""

}