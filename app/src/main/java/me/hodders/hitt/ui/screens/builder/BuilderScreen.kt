package me.hodders.hitt.ui.screens.builder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hodders.hitt.data.model.Workout
import me.hodders.hitt.data.model.WeightType
import me.hodders.hitt.ui.theme.*
import me.hodders.hitt.utils.EXERCISE_LIBRARY

@Composable
fun BuilderScreen(workout: Workout?, onBack: () -> Unit) {
    val viewModel = remember { BuilderViewModel(workout) }
    val context = LocalContext.current

    if (viewModel.showLibrary) {
        ExerciseLibraryDialog(
            currentExercises = viewModel.exercises.map { it.name },
            onSelect = { viewModel.addExercise(it) },
            onDismiss = { viewModel.showLibrary = false }
        )
    }

    viewModel.saveError?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.saveError = null },
            confirmButton = {
                TextButton(onClick = { viewModel.saveError = null }) { Text("OK") }
            },
            text = { Text(error, color = TextPrimary) },
            containerColor = CardBackground
        )
    }

    Scaffold(containerColor = Background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Top bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextSecondary)
                }
                Text(
                    text = if (viewModel.isNew) "New Workout" else "Edit Workout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { viewModel.save(context, onBack) },
                    colors = ButtonDefaults.buttonColors(containerColor = PhaseOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Name
            SectionLabel("Name")
            HiitTextField(value = viewModel.name, onValueChange = { viewModel.name = it }, placeholder = "e.g. Morning Blast")

            Spacer(Modifier.height(20.dp))

            // Color picker
            SectionLabel("Color")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WorkoutColors.forEach { hex ->
                    val color = runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(PhaseOrange)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (viewModel.color == hex)
                                    Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            )
                            .clickable { viewModel.color = hex }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Sets / timing
            SectionLabel("Sets & Timing")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField("Sets", viewModel.sets, { viewModel.sets = it }, Modifier.weight(1f))
                NumberField("Work (s)", viewModel.workDuration, { viewModel.workDuration = it }, Modifier.weight(1f))
                NumberField("Rest (s)", viewModel.restDuration, { viewModel.restDuration = it }, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // Weights (optional)
            SectionLabel("Weights (optional)")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HiitTextField(
                    value = viewModel.heavyWeight,
                    onValueChange = { viewModel.heavyWeight = it },
                    placeholder = "Heavy (e.g. 20kg)",
                    modifier = Modifier.weight(1f)
                )
                HiitTextField(
                    value = viewModel.lightWeight,
                    onValueChange = { viewModel.lightWeight = it },
                    placeholder = "Light (e.g. 10kg)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Exercises
            SectionLabel("Exercises")
            viewModel.exercises.forEachIndexed { index, exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(exercise.name, color = TextPrimary, modifier = Modifier.weight(1f), fontSize = 15.sp)

                    // Weight toggle
                    val weightLabel = when (exercise.weight) {
                        WeightType.HEAVY -> "H"
                        WeightType.LIGHT -> "L"
                        null -> "·"
                    }
                    val weightColor = when (exercise.weight) {
                        WeightType.HEAVY -> PhaseOrange
                        WeightType.LIGHT -> PhaseTeal
                        null -> TextTertiary
                    }
                    TextButton(onClick = { viewModel.cycleWeight(index) }) {
                        Text(weightLabel, color = weightColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    IconButton(onClick = { viewModel.removeExercise(index) }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextTertiary, modifier = Modifier.size(18.dp))
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            }

            Spacer(Modifier.height(12.dp))

            // Add custom exercise
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                HiitTextField(
                    value = viewModel.customExercise,
                    onValueChange = { viewModel.customExercise = it },
                    placeholder = "Custom exercise…",
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        viewModel.addExercise(viewModel.customExercise)
                        viewModel.customExercise = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add", color = PhaseOrange, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { viewModel.showLibrary = true }) {
                Text("Browse exercise library", color = PhaseOrange, fontSize = 14.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ExerciseLibraryDialog(
    currentExercises: List<String>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done", color = PhaseOrange) }
        },
        title = { Text("Exercise Library", color = TextPrimary, fontWeight = FontWeight.Bold) },
        containerColor = CardBackground,
        text = {
            LazyColumn(modifier = Modifier.height(400.dp)) {
                itemsIndexed(EXERCISE_LIBRARY) { _, name ->
                    val already = currentExercises.contains(name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(name) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, color = TextPrimary, modifier = Modifier.weight(1f), fontSize = 15.sp)
                        if (already) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PhaseOrange, modifier = Modifier.size(16.dp))
                        }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                }
            }
        }
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextTertiary,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun HiitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextTertiary, fontSize = 14.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardBackground,
            unfocusedContainerColor = CardBackground,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = PhaseOrange,
            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
            cursorColor = PhaseOrange
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun NumberField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextTertiary, fontSize = 12.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardBackground,
            unfocusedContainerColor = CardBackground,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = PhaseOrange,
            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
            focusedLabelColor = PhaseOrange,
            unfocusedLabelColor = TextTertiary,
            cursorColor = PhaseOrange
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        modifier = modifier
    )
}

// --- Previews ---

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, showSystemUi = true, name = "Builder — New workout")
@Composable
private fun BuilderNewPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        BuilderScreen(workout = null, onBack = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, showSystemUi = true, name = "Builder — Edit workout")
@Composable
private fun BuilderEditPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        BuilderScreen(workout = me.hodders.hitt.utils.DEFAULT_WORKOUTS.last(), onBack = {})
    }
}
