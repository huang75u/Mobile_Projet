package com.example.mobile_projet.data.firebase

data class FriendUser(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String? = null
)

data class FriendActivity(
    val id: String = "",
    val sportType: String = "",
    val calories: Int = 0,
    val timestamp: Long = 0L
)

data class FriendPreview(
    val user: FriendUser,
    val todayActivityNames: List<String>,
    val hasMore: Boolean
)


