package com.example.sleepontrack_app.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class SleepSession(
    val sleepTime: String = "",
    val wakeUpTime: String = "",
    val notificationTime: String = "",
    val sleepQuality: Int = 0,
    val notes: String = "",
    val date: String = ""  // format: "YYYY-MM-DD"
)

class FirestoreManagement {
    private val mFireStore = FirebaseFirestore.getInstance()

    suspend fun registerOrUpdateUser(user: User) {
        mFireStore.collection("users").document(user.id).set(user).await()
    }

    suspend fun loadUser(userId: String): User? {
        val doc = mFireStore.collection("users").document(userId).get().await()
        return doc.data?.let { User.fromMap(it) }
    }

    suspend fun saveSleepSession(userId: String, session: SleepSession) {
        mFireStore.collection("users")
            .document(userId)
            .collection("sleepSessions")
            .document(session.date)
            .set(session)
            .await()
    }

    suspend fun loadSleepSession(userId: String, date: String): SleepSession? {
        val doc = mFireStore.collection("users")
            .document(userId)
            .collection("sleepSessions")
            .document(date)
            .get()
            .await()
        return doc.toObject(SleepSession::class.java)
    }

    suspend fun loadAllSleepSessions(userId: String): List<SleepSession> {
        val snapshot = mFireStore.collection("users")
            .document(userId)
            .collection("sleepSessions")
            .get()
            .await()
        return snapshot.toObjects(SleepSession::class.java)
    }

    suspend fun deleteSleepSession(userId: String, date: String) {
        mFireStore.collection("users")
            .document(userId)
            .collection("sleepSessions")
            .document(date)
            .delete()
            .await()
    }

    fun saveNotificationSettings(
        userId: String,
        time: String,
        enabled: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        val settings = mapOf(
            "time" to time,
            "enabled" to enabled
        )

        FirebaseFirestore.getInstance()
            .collection("notificationSettings")
            .document(userId)
            .set(settings)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun loadNotificationSettings(
        userId: String,
        onResult: (String?, Boolean) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("notificationSettings")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val time = document.getString("time")
                val enabled = document.getBoolean("enabled") ?: false
                onResult(time, enabled)
            }
            .addOnFailureListener {
                onResult(null, false)
            }
    }


}
