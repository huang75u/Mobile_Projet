package com.example.mobile_projet.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_projet.data.firebase.FriendPreview
import com.example.mobile_projet.data.firebase.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FriendsUiState {
    object Loading: FriendsUiState()
    data class Ready(
        val currentUid: String,
        val friends: List<FriendPreview>
    ): FriendsUiState()
    data class Error(val message: String): FriendsUiState()
}

class FriendsViewModel(application: Application): AndroidViewModel(application) {
    private val repo = FriendsRepository()
    private val _state = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val state: StateFlow<FriendsUiState> = _state.asStateFlow()
    
    init { refresh() }
    
    fun refresh() {
        viewModelScope.launch {
            try {
                _state.value = FriendsUiState.Loading
                val uid = repo.signInAnonymouslyIfNeeded()
                val friendUids = repo.getFriends(uid)
                val previews = friendUids.mapNotNull { repo.getFriendPreview(it) }
                _state.value = FriendsUiState.Ready(currentUid = uid, friends = previews)
            } catch (e: Exception) {
                _state.value = FriendsUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
    
    fun addFriend(friendUid: String) {
        val current = _state.value
        if (current is FriendsUiState.Ready) {
            viewModelScope.launch {
                try {
                    repo.addFriend(current.currentUid, friendUid)
                    refresh()
                } catch (e: Exception) {
                    _state.value = FriendsUiState.Error(e.message ?: "Erreur inconnue")
                }
            }
        }
    }
}


