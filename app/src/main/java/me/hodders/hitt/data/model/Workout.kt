package me.hodders.hitt.data.model

enum class WeightType { HEAVY, LIGHT }

enum class TimerPhase { IDLE, COUNTDOWN, WORK, REST, DONE }

data class Exercise(
    val name: String,
    val weight: WeightType? = null
)

data class Workout(
    val id: String,
    val name: String,
    val sets: Int,
    val workDuration: Int,
    val restDuration: Int,
    val exercises: List<Exercise>,
    val color: String,
    val heavyWeight: String? = null,
    val lightWeight: String? = null
)
