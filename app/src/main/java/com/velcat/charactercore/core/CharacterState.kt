package com.velcat.charactercore.core

enum class CharacterState(val priority: Int) {
    IDLE(10),
    WALK(20),
    SLEEP(30),
    MUSIC(40),
    CHARGING(50),
    BATTERY_LOW(60),
    WORKING(100)
}
