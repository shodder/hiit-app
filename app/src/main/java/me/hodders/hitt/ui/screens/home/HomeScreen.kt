package me.hodders.hitt.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hodders.hitt.data.WorkoutRepository
import me.hodders.hitt.data.model.Workout
import me.hodders.hitt.ui.theme.Background
import me.hodders.hitt.ui.theme.CardBackground
import me.hodders.hitt.ui.theme.PhaseOrange
import me.hodders.hitt.ui.theme.TextPrimary
import me.hodders.hitt.ui.theme.TextSecondary
import me.hodders.hitt.ui.theme.TextTertiary
import me.hodders.hitt.utils.formatTime
import me.hodders.hitt.utils.getTotalDuration

@Composable
fun HomeScreen(
    onStartWorkout: (Workout) -> Unit,
    onCreateWorkout: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { WorkoutRepository(context) }
    var workouts by remember { mutableStateOf(repository.loadWorkouts()) }

    // Refresh when screen becomes active
    LaunchedEffect(Unit) {
        workouts = repository.loadWorkouts()
    }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateWorkout,
                containerColor = PhaseOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "New workout")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                Text(
                    text = "HIIT Timer",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(workouts) { workout ->
                WorkoutCard(
                    workout = workout,
                    onClick = { onStartWorkout(workout) }
                )
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}

@Composable
internal fun WorkoutCard(workout: Workout, onClick: () -> Unit) {
    val accentColor = runCatching { Color(android.graphics.Color.parseColor(workout.color)) }
        .getOrDefault(PhaseOrange)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .clickable(onClick = onClick)
    ) {
        // Coloured left border
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(IntrinsicSize.Max)
                .background(accentColor)
                .defaultMinSize(minHeight = 80.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Name row with dot
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = workout.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(8.dp))

            // Stats row
            val total = getTotalDuration(workout)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatChip("${workout.sets} sets")
                StatChip("${workout.workDuration}s work")
                StatChip("${workout.restDuration}s rest")
                StatChip(formatTime(total))
            }

            Spacer(Modifier.height(8.dp))

            // Exercise preview
            Text(
                text = workout.exercises.joinToString(" · ") { it.name },
                fontSize = 13.sp,
                color = TextSecondary,
                maxLines = 2
            )

            // Weight badges
            if (workout.heavyWeight != null || workout.lightWeight != null) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    workout.heavyWeight?.let {
                        WeightBadge(label = "Heavy: $it", color = accentColor)
                    }
                    workout.lightWeight?.let {
                        WeightBadge(label = "Light: $it", color = accentColor.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = TextTertiary,
        modifier = Modifier
            .background(Color(0x22FFFFFF), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun WeightBadge(label: String, color: Color) {
    Text(
        text = label,
        fontSize = 12.sp,
        color = color,
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

// --- Previews ---

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        // Renders using DEFAULT_WORKOUTS (no custom storage needed in preview)
        HomeScreen(onStartWorkout = {}, onCreateWorkout = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, name = "Workout Card — with weights")
@Composable
private fun WorkoutCardWeightsPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        WorkoutCard(
            workout = me.hodders.hitt.utils.DEFAULT_WORKOUTS.last(),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, name = "Workout Card — no weights")
@Composable
private fun WorkoutCardSimplePreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        WorkoutCard(
            workout = me.hodders.hitt.utils.DEFAULT_WORKOUTS.first(),
            onClick = {}
        )
    }
}
