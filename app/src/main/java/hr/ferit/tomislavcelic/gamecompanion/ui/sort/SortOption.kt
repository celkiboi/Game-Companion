package hr.ferit.tomislavcelic.gamecompanion.ui.sort

import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent

enum class SortOption(val label: String) {
    SOON("Expiry ↑"),
    LATE("Expiry ↓"),
    NAME("A → Z"),
    PROGRESS("Progress")
}

fun List<GameEvent>.sortedBy(option: SortOption) = when (option) {
    SortOption.SOON -> sortedBy { it.expires?.seconds ?: Long.MAX_VALUE }
    SortOption.LATE -> sortedByDescending { it.expires?.seconds ?: 0 }
    SortOption.NAME -> sortedBy { it.title.lowercase() }
    SortOption.PROGRESS -> sortedByDescending {
        if (it.challengeGoal == 0) 0f
        else it.currentProgress / it.challengeGoal.toFloat()
    }
}