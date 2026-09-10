package com.velcat.charactercore.power

import android.content.Context
import android.os.BatteryManager
import com.velcat.charactercore.core.CharacterState
import java.io.File

class PowerProfiler(context: Context) {
    private val appContext = context.applicationContext
    private val batteryManager = appContext.getSystemService(BatteryManager::class.java)
    private val logFile = File(appContext.filesDir, "character_power.csv")

    init {
        if (!logFile.exists()) {
            logFile.writeText("timestamp_ms,state,battery_percent,current_now_uA,current_average_uA,charge_counter_uAh,energy_counter_nWh,frame_count,visible\n")
        }
    }

    fun sample(state: CharacterState, frameCount: Long, visible: Boolean, batteryPercent: Int): PowerSample {
        val sample = PowerSample(
            timestampMs = System.currentTimeMillis(),
            state = state,
            batteryPercent = batteryPercent,
            currentNowUa = read(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW),
            currentAverageUa = read(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE),
            chargeCounterUah = read(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER),
            energyCounterNwh = read(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER),
            frameCount = frameCount,
            visible = visible
        )
        append(sample)
        return sample
    }

    private fun read(property: Int): Long? {
        val value = batteryManager.getLongProperty(property)
        return if (value == Long.MIN_VALUE) null else value
    }

    private fun append(s: PowerSample) {
        logFile.appendText(
            listOf(
                s.timestampMs, s.state.name, s.batteryPercent,
                s.currentNowUa ?: "", s.currentAverageUa ?: "",
                s.chargeCounterUah ?: "", s.energyCounterNwh ?: "",
                s.frameCount, s.visible
            ).joinToString(",") + "\n"
        )
    }

    fun logPath(): String = logFile.absolutePath
}
