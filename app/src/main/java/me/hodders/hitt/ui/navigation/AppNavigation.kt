package me.hodders.hitt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import me.hodders.hitt.data.model.Workout
import me.hodders.hitt.ui.screens.builder.BuilderScreen
import me.hodders.hitt.ui.screens.home.HomeScreen
import me.hodders.hitt.ui.screens.timer.TimerScreen
import java.net.URLDecoder
import java.net.URLEncoder

private val gson = Gson()

fun NavController.navigateToTimer(workout: Workout) {
    val json = URLEncoder.encode(gson.toJson(workout), "UTF-8")
    navigate("timer/$json")
}

fun NavController.navigateToBuilder(workout: Workout?) {
    val json = URLEncoder.encode(if (workout != null) gson.toJson(workout) else "null", "UTF-8")
    navigate("builder/$json")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onStartWorkout = { navController.navigateToTimer(it) },
                onCreateWorkout = { navController.navigateToBuilder(null) }
            )
        }

        composable("timer/{workoutJson}") { backStackEntry ->
            val json = URLDecoder.decode(backStackEntry.arguments?.getString("workoutJson") ?: "", "UTF-8")
            val workout = gson.fromJson(json, Workout::class.java)
            TimerScreen(workout = workout, onBack = { navController.popBackStack() })
        }

        composable("builder/{workoutJson}") { backStackEntry ->
            val json = URLDecoder.decode(backStackEntry.arguments?.getString("workoutJson") ?: "null", "UTF-8")
            val workout = if (json == "null") null else gson.fromJson(json, Workout::class.java)
            BuilderScreen(
                workout = workout,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
