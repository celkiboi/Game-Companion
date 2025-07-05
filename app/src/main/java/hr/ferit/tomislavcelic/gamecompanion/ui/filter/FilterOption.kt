package hr.ferit.tomislavcelic.gamecompanion.ui.filter

import hr.ferit.tomislavcelic.gamecompanion.data.model.GameEvent

enum class TimeFilter(val label: String) {
    ALL("All"),
    FUTURE("Future"),
    ONGOING("Ongoing"),
    PAST("Past")
}

enum class CompletionFilter(val label: String) {
    BOTH("Any"),
    COMPLETED("Completed"),
    NOT_DONE("Not completed")
}

fun List<GameEvent>.filtered(
    time: TimeFilter,
    completion: CompletionFilter,
    nowMillis: Long = System.currentTimeMillis()
): List<GameEvent> = filter { ev ->

    val start = ev.starts?.toDate()?.time ?: Long.MIN_VALUE
    val end = ev.expires?.toDate()?.time ?: Long.MAX_VALUE

    val timePass = when (time) {
        TimeFilter.ALL -> true
        TimeFilter.FUTURE -> start > nowMillis
        TimeFilter.ONGOING -> nowMillis in start..<end
        TimeFilter.PAST -> end <= nowMillis
    }

    val compPass = when (completion) {
        CompletionFilter.BOTH-> true
        CompletionFilter.COMPLETED-> ev.solved
        CompletionFilter.NOT_DONE-> !ev.solved
    }

    timePass && compPass
}