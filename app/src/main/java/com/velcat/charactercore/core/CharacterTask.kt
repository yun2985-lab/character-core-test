package com.velcat.charactercore.core

data class CharacterTask(
    val taskId: String,
    val type: String,
    val progress: Float = 0f,
    val message: String? = null
)
