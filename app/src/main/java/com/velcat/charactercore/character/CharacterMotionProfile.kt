package com.velcat.charactercore.character

import com.velcat.charactercore.core.CharacterState

data class MotionProfile(
    val bobAmplitude: Float,
    val bobHz: Float,
    val breatheAmplitude: Float,
    val swayDegrees: Float,
    val walkSpeedPx: Float,
    val frameMs: Long
)

object CharacterMotionProfiles {
    fun forState(characterId: String, state: CharacterState): MotionProfile {
        val velket = characterId == "velket"
        return when (state) {
            CharacterState.SLEEP -> MotionProfile(2f, .22f, .018f, if (velket) 1.2f else .5f, 0f, 650)
            CharacterState.CHARGING -> MotionProfile(7f, .72f, .026f, 1.5f, 0f, 150)
            CharacterState.BATTERY_LOW -> MotionProfile(2f, .28f, .012f, 2.2f, 0f, 420)
            CharacterState.WORKING -> MotionProfile(6f, .9f, .018f, if (velket) 2.8f else 1.8f, 0f, 95)
            CharacterState.WALK -> MotionProfile(if (velket) 10f else 5f, if (velket) .8f else 1.4f, .012f, 2f, if (velket) 82f else 105f, 110)
            CharacterState.MUSIC -> MotionProfile(9f, 1.25f, .02f, 4f, 0f, 120)
            else -> MotionProfile(if (velket) 8f else 4f, .45f, .015f, if (velket) 2.2f else .8f, 0f, 180)
        }
    }
}
