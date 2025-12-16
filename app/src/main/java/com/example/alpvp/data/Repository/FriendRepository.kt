package com.example.alpvp.data.repository

import com.example.alpvp.ui.model.Friend
import com.example.alpvp.ui.model.FriendStatus
import com.example.alpvp.ui.model.FoodLog
import com.example.alpvp.data.Service.AppService
import com.example.alpvp.data.Service.FriendRequest
import com.example.alpvp.data.Service.FriendStatusUpdate

class FriendRepository(private val appService: AppService) {

    suspend fun getFriends(userId: Int): List<Friend> {
        return appService.getUserFriends(userId)
    }

    suspend fun getFriendsFoodLogs(userId: Int): List<FoodLog> {
        return appService.getFriendsFoodLogs(userId)
    }

    suspend fun sendFriendRequest(requesterId: Int, addresseeId: Int): Friend {
        return appService.sendFriendRequest(FriendRequest(requesterId, addresseeId))
    }

    suspend fun updateFriendStatus(friendId: Int, status: FriendStatus): Friend {
        return appService.updateFriendStatus(friendId, FriendStatusUpdate(status))
    }
}

