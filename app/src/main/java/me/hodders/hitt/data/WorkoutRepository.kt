package me.hodders.hitt.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.hodders.hitt.data.model.Workout
import me.hodders.hitt.utils.DEFAULT_WORKOUTS

private const val PREFS_NAME = "hiit_prefs"
private const val KEY_CUSTOM_WORKOUTS = "custom_workouts"

class WorkoutRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun loadWorkouts(): List<Workout> {
        val json = prefs.getString(KEY_CUSTOM_WORKOUTS, null)
        val custom: List<Workout> = if (json != null) {
            val type = object : TypeToken<List<Workout>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
        return DEFAULT_WORKOUTS + custom
    }

    fun saveWorkout(workout: Workout, isNew: Boolean) {
        val json = prefs.getString(KEY_CUSTOM_WORKOUTS, null)
        val type = object : TypeToken<List<Workout>>() {}.type
        val current: MutableList<Workout> = if (json != null) {
            gson.fromJson<List<Workout>>(json, type)?.toMutableList() ?: mutableListOf()
        } else {
            mutableListOf()
        }

        if (isNew) {
            current.add(workout)
        } else {
            val index = current.indexOfFirst { it.id == workout.id }
            if (index >= 0) current[index] = workout else current.add(workout)
        }

        prefs.edit().putString(KEY_CUSTOM_WORKOUTS, gson.toJson(current)).apply()
    }
}
