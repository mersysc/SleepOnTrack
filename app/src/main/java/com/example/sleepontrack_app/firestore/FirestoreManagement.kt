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
}
