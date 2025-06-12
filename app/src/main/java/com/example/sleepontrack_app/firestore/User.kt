package com.example.sleepontrack_app.firestore
data class User(
    val id: String = "",
    val nickname: String = "",
    val email: String = ""
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): User {
            return User(
                id = data["id"] as? String ?: "",
                nickname = data["nickname"] as? String ?: "",
                email = data["email"] as? String ?: ""
            )
        }
    }
}

