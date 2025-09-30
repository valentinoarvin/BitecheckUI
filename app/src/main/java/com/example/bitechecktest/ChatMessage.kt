package com.example.bitechecktest

data class ChatMessage(
    val text: String,
    val sender: Sender
)

enum class Sender {
    USER, AI
}