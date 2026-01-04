package com.example.alpvp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.alpvp.data.dto.UserProfileData
import com.example.alpvp.data.repository.FriendRepository
import com.example.alpvp.ui.model.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendViewModel(
    private val friendRepository: FriendRepository
) : ViewModel() {
    private val _addFriends = MutableStateFlow<List<UserProfileData>>(listOf(UserProfileData()))

    val addFriend: StateFlow<List<UserProfileData>> = _addFriends

    init {
        searchFriends()
    }

    fun searchFriends(email: String){
        _addFriends.value =_addFriends.value.
    }
}