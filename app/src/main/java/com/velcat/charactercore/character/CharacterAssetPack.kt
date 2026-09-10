package com.velcat.charactercore.character

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.velcat.charactercore.BuildConfig
import com.velcat.charactercore.R
import com.velcat.charactercore.core.CharacterState

interface CharacterAssetPack {
    val id: String
    val displayName: String
    fun frame(context: Context, state: CharacterState, elapsedMs: Long): Bitmap
}

object CharacterAssetFactory {
    fun create(): CharacterAssetPack = when (BuildConfig.CHARACTER_ID) {
        "starcat" -> StarCatAssetPack
        else -> VelketAssetPack
    }
}

private fun decode(context: Context, id: Int): Bitmap =
    BitmapFactory.decodeResource(context.resources, id)

object VelketAssetPack : CharacterAssetPack {
    override val id = "velket"
    override val displayName = "벨켓"
    private val cache = mutableMapOf<Int, Bitmap>()
    override fun frame(context: Context, state: CharacterState, elapsedMs: Long): Bitmap {
        val res = when (state) {
            CharacterState.SLEEP -> R.drawable.velket_sleep
            CharacterState.CHARGING -> R.drawable.velket_charging
            CharacterState.WORKING -> R.drawable.velket_working
            CharacterState.WALK, CharacterState.MUSIC -> R.drawable.velket_action
            else -> R.drawable.velket_idle
        }
        return cache.getOrPut(res) { decode(context, res) }
    }
}

object StarCatAssetPack : CharacterAssetPack {
    override val id = "starcat"
    override val displayName = "스타캣"
    private val ids = intArrayOf(
        R.drawable.starcat_00, R.drawable.starcat_01, R.drawable.starcat_02,
        R.drawable.starcat_03, R.drawable.starcat_04, R.drawable.starcat_05,
        R.drawable.starcat_06, R.drawable.starcat_07, R.drawable.starcat_08,
        R.drawable.starcat_09, R.drawable.starcat_10, R.drawable.starcat_11
    )
    private val cache = mutableMapOf<Int, Bitmap>()
    override fun frame(context: Context, state: CharacterState, elapsedMs: Long): Bitmap {
        val speed = when (state) {
            CharacterState.SLEEP -> 700L
            CharacterState.WORKING -> 95L
            CharacterState.WALK -> 110L
            else -> 180L
        }
        val res = ids[((elapsedMs / speed) % ids.size).toInt()]
        return cache.getOrPut(res) { decode(context, res) }
    }
}
