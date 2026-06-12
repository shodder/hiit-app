package me.hodders.hitt.utils

import me.hodders.hitt.data.model.Workout

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

fun getTotalDuration(workout: Workout): Int =
    workout.sets * workout.exercises.size * (workout.workDuration + workout.restDuration)
