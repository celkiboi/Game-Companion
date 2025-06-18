package hr.ferit.tomislavcelic.gamecompanion.ui.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EventDetailVMFactory(private val eventId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EventDetailViewModel(eventId) as T
}