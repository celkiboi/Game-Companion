package hr.ferit.tomislavcelic.gamecompanion.ui.screens.allevents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent
import hr.ferit.tomislavcelic.gamecompanion.data.repository.AuthRepository
import hr.ferit.tomislavcelic.gamecompanion.data.repository.EventRepository
import hr.ferit.tomislavcelic.gamecompanion.ui.filter.CompletionFilter
import hr.ferit.tomislavcelic.gamecompanion.ui.filter.TimeFilter
import hr.ferit.tomislavcelic.gamecompanion.ui.filter.filtered
import hr.ferit.tomislavcelic.gamecompanion.ui.sort.SortOption
import hr.ferit.tomislavcelic.gamecompanion.ui.sort.sortedBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AllEventsViewModel(
    authRepo: AuthRepository = AuthRepository(),
    eventRepo: EventRepository = EventRepository()
) : ViewModel() {

    private val _sort = MutableStateFlow(SortOption.SOON)
    val sort: StateFlow<SortOption> = _sort

    private val _timeFilter = MutableStateFlow(TimeFilter.ALL)
    private val _completionFilter = MutableStateFlow(CompletionFilter.BOTH)
    val timeFilter : StateFlow<TimeFilter> = _timeFilter
    val completionFilter : StateFlow<CompletionFilter> = _completionFilter

    private val _events = MutableStateFlow<List<GameEvent>>(emptyList())
    val events: StateFlow<List<GameEvent>> = combine(
        _events, _sort, _timeFilter, _completionFilter
    ) { list, sortOpt, timeF, compF ->
        list
            .filtered(timeF, compF)
            .sortedBy(sortOpt)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        authRepo.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                eventRepo.observeUserEvents(uid)
                    .collect { _events.value = it }
            }
        }
    }

    fun setSort(opt: SortOption) { _sort.value = opt }
    fun setTimeFilter(f: TimeFilter) { _timeFilter.value = f }

}