package com.example.mobile_projet.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FriendsRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
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
                "displayName" to "Pseudo",
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

    // 更新昵称
    suspend fun updateDisplayName(uid: String, name: String) {
        userDoc(uid).update(mapOf(
            "displayName" to name,
            "updatedAt" to System.currentTimeMillis()
        )).await()
    }

    // 上传头像并保存URL
    suspend fun uploadAvatarAndSave(uid: String, bytes: ByteArray): String {
        // 优先使用 google-services.json 中的 storageBucket，避免默认桶未解析导致 404
        val bucket = storage.app.options.storageBucket
        val rootRef = if (!bucket.isNullOrBlank()) {
            storage.getReferenceFromUrl("gs://$bucket")
        } else {
            storage.reference
        }
        val ref = rootRef.child("avatars/$uid.jpg")
        ref.putBytes(bytes).await()
        val url = ref.downloadUrl.await().toString()
        userDoc(uid).update(mapOf(
            "photoUrl" to url,
            "updatedAt" to System.currentTimeMillis()
        )).await()
        return url
    }

    // 添加一条当日训练项目
    suspend fun addActivity(uid: String, sportType: String, calories: Int, timestamp: Long = System.currentTimeMillis()) {
        val data = mapOf(
            "sportType" to sportType,
            "calories" to calories,
            "timestamp" to timestamp
        )
        activitiesCollection(uid).add(data).await()
    }

    // 以本地 SportGoal.id 作为文档ID进行写入/更新（便于后续修改/删除同步）
    suspend fun upsertActivity(uid: String, goalId: String, sportType: String, calories: Int, timestamp: Long = System.currentTimeMillis()) {
        val data = mapOf(
            "sportType" to sportType,
            "calories" to calories,
            "timestamp" to timestamp
        )
        activitiesCollection(uid).document(goalId).set(data).await()
    }

    // 删除对应 SportGoal 的活动
    suspend fun deleteActivity(uid: String, goalId: String) {
        activitiesCollection(uid).document(goalId).delete().await()
    }

    // 获取当日卡路里汇总
    suspend fun getTodayCalories(uid: String): Int {
        return getTodayActivities(uid).sumOf { it.calories }
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

    // 获取今天卡路里（批量辅助方法）
    suspend fun getTodayCaloriesForUids(uids: List<String>): List<Pair<String, Int>> {
        val results = mutableListOf<Pair<String, Int>>()
        for (id in uids) {
            val kcal = getTodayCalories(id)
            results.add(id to kcal)
        }
        return results
    }
}


