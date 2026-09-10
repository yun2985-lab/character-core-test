package com.velcat.charactercore.core

import kotlin.random.Random

class CharacterBrain {
    private var forcedState: CharacterState? = null
    private var charging = false
    private var batteryPercent = 100
    private var activeTask: CharacterTask? = null
    private var lastIdleDecisionMs = 0L
    private var ambientState = CharacterState.IDLE

    fun setCharging(value: Boolean) { charging = value }
    fun setBatteryPercent(value: Int) { batteryPercent = value.coerceIn(0, 100) }

    fun setTask(task: CharacterTask?) {
        activeTask = task
    }

    fun forceState(state: CharacterState?) {
        forcedState = state
    }

    fun currentState(nowMs: Long = System.currentTimeMillis()): CharacterState {
        forcedState?.let { return it }
        if (activeTask != null) return CharacterState.WORKING
        if (batteryPercent <= 15 && !charging) return CharacterState.BATTERY_LOW
        if (charging) return CharacterState.CHARGING

        if (nowMs - lastIdleDecisionMs > 12_000L) {
            lastIdleDecisionMs = nowMs
            ambientState = if (Random.nextFloat() < 0.25f) CharacterState.WALK else CharacterState.IDLE
        }
        return ambientState
    }

    fun taskProgress(): Float = activeTask?.progress?.coerceIn(0f, 1f) ?: 0f
}
