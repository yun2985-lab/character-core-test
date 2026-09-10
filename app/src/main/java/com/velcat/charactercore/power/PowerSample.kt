package com.velcat.charactercore.power

import com.velcat.charactercore.core.CharacterState

data class PowerSample(
    val timestampMs: Long,
    val state: CharacterState,
    val batteryPercent: Int,
    val currentNowUa: Long?,
    val currentAverageUa: Long?,
    val chargeCounterUah: Long?,
    val energyCounterNwh: Long?,
    val frameCount: Long,
    val visible: Boolean
)
