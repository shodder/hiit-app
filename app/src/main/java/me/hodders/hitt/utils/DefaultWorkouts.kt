package me.hodders.hitt.utils

import me.hodders.hitt.data.model.Exercise
import me.hodders.hitt.data.model.WeightType
import me.hodders.hitt.data.model.Workout

val DEFAULT_WORKOUTS = listOf(
    Workout(
        id = "default_1",
        name = "Tabata Classic",
        sets = 2,
        workDuration = 20,
        restDuration = 10,
        exercises = listOf(
            Exercise("Burpees"),
            Exercise("Jump Squats"),
            Exercise("Mountain Climbers"),
            Exercise("High Knees")
        ),
        color = "#FF6B35"
    ),
    Workout(
        id = "default_2",
        name = "EMOM 10",
        sets = 3,
        workDuration = 50,
        restDuration = 10,
        exercises = listOf(
            Exercise("Push-ups"),
            Exercise("Air Squats"),
            Exercise("Sit-ups")
        ),
        color = "#4ECDC4"
    ),
    Workout(
        id = "default_3",
        name = "Power HIIT",
        sets = 2,
        workDuration = 40,
        restDuration = 20,
        exercises = listOf(
            Exercise("Box Jumps"),
            Exercise("Kettlebell Swings"),
            Exercise("Burpees"),
            Exercise("Sprint"),
            Exercise("Pull-ups")
        ),
        color = "#A855F7"
    ),
    Workout(
        id = "default_4",
        name = "Core Blast",
        sets = 2,
        workDuration = 30,
        restDuration = 15,
        exercises = listOf(
            Exercise("Plank"),
            Exercise("Bicycle Crunches"),
            Exercise("Leg Raises"),
            Exercise("Russian Twists"),
            Exercise("Crunches")
        ),
        color = "#22C55E"
    ),
    Workout(
        id = "default_5",
        name = "Upper Body",
        sets = 2,
        workDuration = 10,
        restDuration = 5,
        heavyWeight = "16kg",
        lightWeight = "10kg",
        exercises = listOf(
            Exercise("Bicep Curls", WeightType.HEAVY),
            Exercise("Shoulder Press", WeightType.HEAVY),
            Exercise("Russian Twists", WeightType.LIGHT),
            Exercise("Chest Press", WeightType.HEAVY)
        ),
        color = "#F59E0B"
    )
)

val EXERCISE_LIBRARY = listOf(
    "Air Squats", "Bear Crawls", "Bicep Curls", "Bicycle Crunches",
    "Box Jumps", "Burpees", "Crunches", "Dips", "High Knees",
    "Inchworms", "Jump Rope", "Jump Squats", "Jumping Jacks",
    "Kettlebell Swings", "Leg Raises", "Lunges", "Mountain Climbers",
    "Plank", "Pull-ups", "Push-ups", "Reverse Lunges", "Russian Twists",
    "Shoulder Press", "Side Shuffles", "Sit-ups", "Skaters", "Sprint",
    "Tricep Dips"
)
