package com.example.mobile_projet.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_projet.data.firebase.FriendUser
import com.example.mobile_projet.data.firebase.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaderboardEntry(
    val user: FriendUser,
    val calories: Int,
    val isMe: Boolean
)

sealed class LeaderboardUiState {
    object Loading: LeaderboardUiState()
    data class Ready(val entries: List<LeaderboardEntry>): LeaderboardUiState()
    data class Error(val message: String): LeaderboardUiState()
}

class LeaderboardViewModel(application: Application): AndroidViewModel(application) {
    private val repo = FriendsRepository()
    private val _state = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val state: StateFlow<LeaderboardUiState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            try {
                _state.value = LeaderboardUiState.Loading
                val me = repo.signInAnonymouslyIfNeeded()
                val friendUids = repo.getFriends(me)
                val allUids = listOf(me) + friendUids

                // 获取用户信息与卡路里
                val entries = mutableListOf<LeaderboardEntry>()
                for (uid in allUids) {
                    val user = repo.getFriendUser(uid) ?: continue
                    val kcal = repo.getTodayCalories(uid)
                    entries.add(
                        LeaderboardEntry(user = user, calories = kcal, isMe = uid == me)
                    )
                }
                // 按卡路里倒序
                _state.value = LeaderboardUiState.Ready(entries.sortedByDescending { it.calories })
            } catch (e: Exception) {
                _state.value = LeaderboardUiState.Error(e.message ?: "Erreur")
            }
        }
    }
}


