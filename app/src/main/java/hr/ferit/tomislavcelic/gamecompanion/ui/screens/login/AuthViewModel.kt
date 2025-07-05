package hr.ferit.tomislavcelic.gamecompanion.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import hr.ferit.tomislavcelic.gamecompanion.data.repository.AuthRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import hr.ferit.tomislavcelic.gamecompanion.notifications.EventAlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import hr.ferit.tomislavcelic.gamecompanion.GameCompanionApp
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository(),
    private val eventRepo: EventRepository = EventRepository(),
    private val scheduler: EventAlarmScheduler = EventAlarmScheduler(
        GameCompanionApp.appContext
    )
) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val user: StateFlow<FirebaseUser?> =
        repo.authState.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            repo.currentUser
        )

    val displayName: StateFlow<String> = user
        .map { u ->
            u?.displayName?.takeIf { it.isNotBlank() }
                ?: u?.email
                ?: "Guest"
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Guest")

    init {
        viewModelScope.launch {
            user.collectLatest { firebaseUser ->
                if (firebaseUser != null) {
                    scheduler.cancelAll(firebaseUser.uid)

                    scheduler.start(
                        firebaseUser.uid,
                        eventRepo.observeUserEvents(firebaseUser.uid)
                    )
                }
            }
        }
    }

    fun login(email: String, pass: String) = viewModelScope.launch {
        try {
            repo.login(email, pass)
            _error.value = null
        } catch (e: Exception) {
            _error.value = friendlyMessage(e)
        }
    }

    fun register(name: String, email: String, pass: String) = viewModelScope.launch {
        try {
            repo.register(email, pass, name)
            _error.value = null
        } catch (e: Exception) {
            _error.value = friendlyMessage(e)
        }
    }

    fun logout() = repo.logout()

    private fun friendlyMessage(e: Exception) = when (e) {
        is FirebaseAuthInvalidCredentialsException ->
            "Wrong password or malformed e-mail."
        is FirebaseAuthInvalidUserException ->
            "No user record with that e-mail."
        is FirebaseAuthWeakPasswordException ->
            "Password must be at least 6 characters."
        else -> e.localizedMessage ?: "Unknown error"
    }
}