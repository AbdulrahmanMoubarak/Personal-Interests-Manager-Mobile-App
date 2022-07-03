package com.decodetalkers.personalinterestsmanager.models

data class ChatMessageModel (
    var messageId: Int,
    var message: String,
    var isChatbotMessage: Boolean
)