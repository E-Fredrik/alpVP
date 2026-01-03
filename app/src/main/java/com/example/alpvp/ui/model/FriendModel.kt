package com.example.alpvp.ui.model

data class Friend(
    val friendId: Int,
    val userId: Int,
    val friendUserId: Int,
    val status: FriendStatus,
    val createdAt: String,
    val username: String? = null
)

enum class FriendStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    BLOCKED
}