package me.hodders.hitt.ui.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.hodders.hitt.data.model.TimerPhase
import me.hodders.hitt.data.model.Workout
import me.hodders.hitt.ui.components.CircularProgressTimer
import me.hodders.hitt.ui.theme.*
import me.hodders.hitt.utils.formatTime

@Composable
fun TimerScreen(workout: Workout, onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel = remember { TimerViewModel(workout, context) }

    val phase by viewModel.phase.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val currentSet by viewModel.currentSet.collectAsState()
    val currentExercise by viewModel.currentExercise.collectAsState()
    val nextExercise by viewModel.nextExercise.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val totalElapsed by viewModel.totalElapsed.collectAsState()

    val phaseColor = when (phase) {
        TimerPhase.IDLE -> PhaseGray
        TimerPhase.COUNTDOWN -> PhaseYellow
        TimerPhase.WORK -> PhaseOrange
        TimerPhase.REST -> PhaseTeal
        TimerPhase.DONE -> PhaseGreen
    }

    Scaffold(containerColor = Background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.stop()
                    onBack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary
                    )
                }
                Text(
                    text = workout.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            when (phase) {
                TimerPhase.IDLE -> IdleContent(workout = workout)

                TimerPhase.DONE -> DoneContent(
                    totalElapsed = totalElapsed,
                    onRestart = { viewModel.start() }
                )

                else -> {
                    // Set progress dots
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(workout.sets) { i ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i < currentSet - 1) phaseColor
                                        else if (i == currentSet - 1) phaseColor.copy(alpha = 0.5f)
                                        else Color.White.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Phase label
                    val phaseLabel = when (phase) {
                        TimerPhase.COUNTDOWN -> "GET READY"
                        TimerPhase.WORK -> "WORK"
                        TimerPhase.REST -> "REST"
                        else -> ""
                    }
                    Text(
                        text = phaseLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = phaseColor,
                        letterSpacing = 3.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Circular timer
                    val circleLabel = when (phase) {
                        TimerPhase.COUNTDOWN -> nextExercise?.name
                        TimerPhase.WORK -> currentExercise?.name
                        TimerPhase.REST -> currentExercise?.name
                        else -> null
                    }
                    val weightLabel = when {
                        phase == TimerPhase.WORK -> when (currentExercise?.weight) {
                            me.hodders.hitt.data.model.WeightType.HEAVY ->
                                workout.heavyWeight?.let { "heavy  •  $it" }
                            me.hodders.hitt.data.model.WeightType.LIGHT ->
                                workout.lightWeight?.let { "light  •  $it" }
                            null -> null
                        }
                        else -> null
                    }

                    CircularProgressTimer(
                        progress = progress,
                        color = phaseColor,
                        timeLeft = timeLeft,
                        label = circleLabel,
                        sublabel = weightLabel
                    )

                    Spacer(Modifier.height(16.dp))

                    // Set / total time info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoChip(label = "Set", value = "$currentSet / ${workout.sets}")
                        InfoChip(label = "Elapsed", value = formatTime(totalElapsed))
                    }

                    // Next up
                    nextExercise?.let { next ->
                        Spacer(Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardBackground, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text("NEXT UP", fontSize = 11.sp, color = TextTertiary, letterSpacing = 2.sp)
                                Text(next.name, fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Controls
            if (phase != TimerPhase.DONE) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (phase != TimerPhase.IDLE && isRunning) {
                        FilledIconButton(
                            onClick = { viewModel.stop(); onBack() },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = TextPrimary
                            ),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Stop")
                        }
                    }

                    Button(
                        onClick = {
                            when {
                                phase == TimerPhase.IDLE -> viewModel.start()
                                isRunning -> viewModel.pause()
                                else -> viewModel.resume()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = phaseColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .widthIn(min = 160.dp)
                    ) {
                        Text(
                            text = when {
                                phase == TimerPhase.IDLE -> "Start"
                                isRunning -> "Pause"
                                else -> "Resume"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun IdleContent(workout: Workout) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${workout.sets} sets  ·  ${workout.workDuration}s work  ·  ${workout.restDuration}s rest",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        workout.exercises.forEachIndexed { index, exercise ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${index + 1}",
                    fontSize = 14.sp,
                    color = TextTertiary,
                    modifier = Modifier.width(28.dp)
                )
                Text(text = exercise.name, fontSize = 16.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                exercise.weight?.let {
                    Text(
                        text = it.name.lowercase(),
                        fontSize = 12.sp,
                        color = PhaseOrange
                    )
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
internal fun DoneContent(totalElapsed: Int, onRestart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 48.dp)
    ) {
        Text("🔥", fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text("DONE!", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = PhaseGreen)
        Spacer(Modifier.height(8.dp))
        Text("Total time: ${formatTime(totalElapsed)}", fontSize = 18.sp, color = TextSecondary)
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = PhaseGreen),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(52.dp).widthIn(min = 140.dp)
        ) {
            Text("Restart", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TextTertiary, letterSpacing = 1.sp)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// --- Previews ---

private val previewWorkout = me.hodders.hitt.utils.DEFAULT_WORKOUTS.first()

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, showSystemUi = true, name = "Timer — Idle")
@Composable
private fun TimerIdlePreview() {
    HiitTheme {
        Scaffold(containerColor = Background) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(64.dp))
                IdleContent(workout = previewWorkout)
                Button(
                    onClick = {},
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PhaseGray),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(56.dp).widthIn(min = 160.dp)
                ) {
                    Text("Start", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, showSystemUi = true, name = "Timer — Work phase")
@Composable
private fun TimerWorkPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        androidx.compose.material3.Scaffold(containerColor = Background) { padding ->
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(64.dp))
                Text("WORK", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PhaseOrange, letterSpacing = 3.sp)
                Spacer(Modifier.height(16.dp))
                me.hodders.hitt.ui.components.CircularProgressTimer(
                    progress = 0.6f,
                    color = PhaseOrange,
                    timeLeft = 12,
                    label = "Burpees"
                )
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    InfoChip("Set", "1 / 2")
                    InfoChip("Elapsed", "0:28")
                }
                Spacer(Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().background(CardBackground, RoundedCornerShape(12.dp)).padding(14.dp)) {
                    androidx.compose.foundation.layout.Column {
                        Text("NEXT UP", fontSize = 11.sp, color = TextTertiary, letterSpacing = 2.sp)
                        Text("Jump Squats", fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(onClick = {}, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PhaseOrange), shape = RoundedCornerShape(16.dp), modifier = Modifier.height(56.dp).widthIn(min = 160.dp)) {
                    Text("Pause", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, showSystemUi = true, name = "Timer — Rest phase")
@Composable
private fun TimerRestPreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        androidx.compose.material3.Scaffold(containerColor = Background) { padding ->
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(64.dp))
                Text("REST", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PhaseTeal, letterSpacing = 3.sp)
                Spacer(Modifier.height(16.dp))
                me.hodders.hitt.ui.components.CircularProgressTimer(
                    progress = 0.3f,
                    color = PhaseTeal,
                    timeLeft = 7,
                    label = "Jump Squats"
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14, showSystemUi = true, name = "Timer — Done")
@Composable
private fun TimerDonePreview() {
    me.hodders.hitt.ui.theme.HiitTheme {
        androidx.compose.material3.Scaffold(containerColor = Background) { padding ->
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DoneContent(totalElapsed = 243, onRestart = {})
            }
        }
    }
}
