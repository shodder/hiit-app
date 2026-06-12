package me.hodders.hitt.ui.screens.builder

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.hodders.hitt.data.WorkoutRepository
import me.hodders.hitt.data.model.Exercise
import me.hodders.hitt.data.model.WeightType
import me.hodders.hitt.data.model.Workout
import me.hodders.hitt.ui.theme.WorkoutColors

class BuilderViewModel(private val initial: Workout?) : ViewModel() {

    var name by mutableStateOf(initial?.name ?: "")
    var sets by mutableStateOf((initial?.sets ?: 3).toString())
    var workDuration by mutableStateOf((initial?.workDuration ?: 40).toString())
    var restDuration by mutableStateOf((initial?.restDuration ?: 20).toString())
    var color by mutableStateOf(initial?.color ?: WorkoutColors.first())
    var heavyWeight by mutableStateOf(initial?.heavyWeight ?: "")
    var lightWeight by mutableStateOf(initial?.lightWeight ?: "")
    var customExercise by mutableStateOf("")
    var showLibrary by mutableStateOf(false)
    var saveError by mutableStateOf<String?>(null)

    val exercises = mutableStateListOf<Exercise>().also { list ->
        initial?.exercises?.let { list.addAll(it) }
    }

    val isNew get() = initial == null

    fun addExercise(name: String) {
        if (name.isNotBlank()) exercises.add(Exercise(name.trim()))
    }

    fun removeExercise(index: Int) {
        if (index in exercises.indices) exercises.removeAt(index)
    }

    fun cycleWeight(index: Int) {
        if (index !in exercises.indices) return
        val ex = exercises[index]
        exercises[index] = ex.copy(
            weight = when (ex.weight) {
                null -> WeightType.HEAVY
                WeightType.HEAVY -> WeightType.LIGHT
                WeightType.LIGHT -> null
            }
        )
    }

    fun save(context: Context, onDone: () -> Unit) {
        if (name.isBlank()) { saveError = "Name is required"; return }
        if (exercises.isEmpty()) { saveError = "Add at least one exercise"; return }

        val workout = Workout(
            id = initial?.id ?: System.currentTimeMillis().toString(),
            name = name.trim(),
            sets = sets.toIntOrNull()?.coerceAtLeast(1) ?: 3,
            workDuration = workDuration.toIntOrNull()?.coerceAtLeast(1) ?: 40,
            restDuration = restDuration.toIntOrNull()?.coerceAtLeast(1) ?: 20,
            exercises = exercises.toList(),
            color = color,
            heavyWeight = heavyWeight.trim().ifBlank { null },
            lightWeight = lightWeight.trim().ifBlank { null }
        )
        WorkoutRepository(context).saveWorkout(workout, isNew)
        onDone()
    }
}
