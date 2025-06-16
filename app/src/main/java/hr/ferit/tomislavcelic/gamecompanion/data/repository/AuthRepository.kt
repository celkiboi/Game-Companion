package hr.ferit.tomislavcelic.gamecompanion.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser
    val authState = callbackFlow<FirebaseUser?> {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun register(email: String, pass: String, name: String) =
        auth.createUserWithEmailAndPassword(email, pass)
            .await()
            .user?.apply {
                updateProfile(userProfileChangeRequest { displayName = name }).await()
            }

    suspend fun login(email: String, pass: String) =
        auth.signInWithEmailAndPassword(email, pass).await()

    fun logout() = auth.signOut()
}