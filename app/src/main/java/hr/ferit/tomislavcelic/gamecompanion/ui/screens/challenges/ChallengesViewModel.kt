package hr.ferit.tomislavcelic.gamecompanion.ui.screens.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.data.repository.AuthRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import hr.ferit.tomislavcelic.gamecompanion.ui.sort.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import hr.ferit.tomislavcelic.gamecompanion.ui.sort.sortedBy

class ChallengesViewModel(
    authRepo: AuthRepository = AuthRepository(),
    eventRepo: EventRepository = EventRepository()
) : ViewModel() {

    private val _sort = MutableStateFlow(SortOption.SOON)
    val sort: StateFlow<SortOption> = _sort

    private val _challenges = MutableStateFlow<List<GameEvent>>(emptyList())
    val challenges: StateFlow<List<GameEvent>> =
        combine(_challenges, _sort) { list, opt -> list.sortedBy(opt) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        authRepo.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                eventRepo.observeUserChallenges(uid)
                    .collect { _challenges.value = it }
            }
        }
    }

    fun setSort(opt: SortOption) { _sort.value = opt }
}