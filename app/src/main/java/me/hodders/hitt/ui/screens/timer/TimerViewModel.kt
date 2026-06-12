package me.hodders.hitt.ui.screens.timer

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.hodders.hitt.R
import me.hodders.hitt.data.model.Exercise
import me.hodders.hitt.data.model.TimerPhase
import me.hodders.hitt.data.model.Workout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel(private val workout: Workout, context: Context) : ViewModel() {

    private val _phase = MutableStateFlow(TimerPhase.IDLE)
    val phase: StateFlow<TimerPhase> = _phase.asStateFlow()

    private val _currentRound = MutableStateFlow(1)
    private val _currentExerciseIndex = MutableStateFlow(0)

    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _totalElapsed = MutableStateFlow(0)
    val totalElapsed: StateFlow<Int> = _totalElapsed.asStateFlow()

    val currentSet: StateFlow<Int> = _currentRound.map { round ->
        ((round - 1) / workout.exercises.size) + 1
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    val currentExercise: StateFlow<Exercise?> = _currentExerciseIndex.map { idx ->
        workout.exercises.getOrNull(idx)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val nextExercise: StateFlow<Exercise?> = combine(_currentRound, _currentExerciseIndex) { round, idx ->
        if (_phase.value == TimerPhase.DONE) null
        else workout.exercises.getOrNull((idx + 1) % workout.exercises.size)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val progress: StateFlow<Float> = combine(_phase, _timeLeft) { phase, timeLeft ->
        when (phase) {
            TimerPhase.IDLE, TimerPhase.DONE -> 0f
            TimerPhase.COUNTDOWN -> 1f - timeLeft / 3f
            TimerPhase.WORK -> 1f - timeLeft / workout.workDuration.toFloat()
            TimerPhase.REST -> 1f - timeLeft / workout.restDuration.toFloat()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    private var timerJob: Job? = null

    @SuppressLint("ServiceCast")
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()
    private var beepSoundId: Int = 0

    init {
        beepSoundId = soundPool.load(context, R.raw.beep, 1)
    }

    fun start() {
        _phase.value = TimerPhase.COUNTDOWN
        _currentRound.value = 1
        _currentExerciseIndex.value = 0
        _totalElapsed.value = 0
        _timeLeft.value = 3
        _isRunning.value = true
        launchTimer()
    }

    fun pause() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun resume() {
        _isRunning.value = true
        launchTimer()
    }

    fun stop() {
        timerJob?.cancel()
        _isRunning.value = false
        _phase.value = TimerPhase.IDLE
        _currentRound.value = 1
        _currentExerciseIndex.value = 0
        _timeLeft.value = 0
        _totalElapsed.value = 0
    }

    private fun launchTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _isRunning.value) {
                delay(1000L)
                if (!_isRunning.value) break
                _totalElapsed.value += 1
                val tl = _timeLeft.value
                if (tl <= 1) {
                    advance()
                } else {
                    _timeLeft.value = tl - 1
                    maybeBeep(tl - 1)
                }
            }
        }
    }

    private fun advance() {
        val totalExercises = workout.exercises.size
        when (_phase.value) {
            TimerPhase.COUNTDOWN -> {
                _phase.value = TimerPhase.WORK
                _timeLeft.value = workout.workDuration
                vibrateDouble()
                maybeBeep(workout.workDuration)
            }
            TimerPhase.WORK -> {
                if (_currentRound.value >= workout.sets * totalExercises) {
                    timerJob?.cancel()
                    _isRunning.value = false
                    _phase.value = TimerPhase.DONE
                    _timeLeft.value = 0
                } else {
                    _phase.value = TimerPhase.REST
                    _timeLeft.value = workout.restDuration
                    vibrateSingle()
                    maybeBeep(workout.restDuration)
                }
            }
            TimerPhase.REST -> {
                _currentRound.value += 1
                _currentExerciseIndex.value = (_currentExerciseIndex.value + 1) % totalExercises
                _phase.value = TimerPhase.WORK
                _timeLeft.value = workout.workDuration
                vibrateDouble()
                maybeBeep(workout.workDuration)
            }
            else -> {}
        }
    }

    private fun maybeBeep(timeLeft: Int) {
        val phase = _phase.value
        val active = phase == TimerPhase.WORK || phase == TimerPhase.REST || phase == TimerPhase.COUNTDOWN
        if (active && timeLeft in 1..3 && beepSoundId != 0) {
            soundPool.play(beepSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    private fun vibrateSingle() {
        vibrator?.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun vibrateDouble() {
        val pattern = longArrayOf(0, 150, 100, 150)
        val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        soundPool.release()
    }
}
