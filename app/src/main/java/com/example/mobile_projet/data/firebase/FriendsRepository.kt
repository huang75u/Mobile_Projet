package com.example.mobile_projet.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FriendsRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun signInAnonymouslyIfNeeded(): String {
        val user = auth.currentUser
        val uid = if (user != null) user.uid else auth.signInAnonymously().await().user!!.uid
        ensureUserDocument(uid)
        return uid
    }
    
    // friends list stored under users/{uid}/friends -> { friendUid: true }
    private fun friendsCollection(uid: String) = db.collection("users").document(uid).collection("friends")
    private fun userDoc(uid: String) = db.collection("users").document(uid)
    private fun activitiesCollection(uid: String) = userDoc(uid).collection("activities")
    
    private suspend fun ensureUserDocument(uid: String) {
        val docRef = userDoc(uid)
        val snap = docRef.get().await()
        if (!snap.exists()) {
            val default = mapOf(
                "displayName" to "",
                "photoUrl" to null,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
            docRef.set(default).await()
        } else {
            // update timestamp for visibility in console
            docRef.update("updatedAt", System.currentTimeMillis()).await()
        }
    }
    
    suspend fun addFriend(currentUid: String, friendUid: String) {
        friendsCollection(currentUid).document(friendUid).set(mapOf("addedAt" to System.currentTimeMillis())).await()
    }
    
    suspend fun getFriends(currentUid: String): List<String> {
        val snap = friendsCollection(currentUid).get().await()
        return snap.documents.map { it.id }
    }
    
    suspend fun getFriendUser(uid: String): FriendUser? {
        val doc = userDoc(uid).get().await()
        return if (doc.exists()) doc.toObject(FriendUser::class.java)?.copy(uid = uid) else null
    }
    
    suspend fun getTodayActivities(uid: String): List<FriendActivity> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val q = activitiesCollection(uid)
            .whereGreaterThanOrEqualTo("timestamp", startOfDay)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val snap = q.get().await()
        return snap.documents.mapNotNull { it.toObject(FriendActivity::class.java)?.copy(id = it.id) }
    }
    
    suspend fun getFriendPreview(uid: String): FriendPreview? {
        val user = getFriendUser(uid) ?: return null
        val acts = getTodayActivities(uid)
        val names = acts.map { it.sportType }
        val previewNames = if (names.size > 2) names.take(2) else names
        return FriendPreview(user = user, todayActivityNames = previewNames, hasMore = names.size > 2)
    }
    
    suspend fun getFriendDetails(uid: String): Pair<FriendUser, List<FriendActivity>>? {
        val user = getFriendUser(uid) ?: return null
        val activities = getTodayActivities(uid)
        return user to activities
    }
}


