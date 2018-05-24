package com.goosvandenbekerom.roulette.cli

import com.goosvandenbekerom.roulette.core.Result

class ClientState {
    var playerId: Long = -1
    var chipAmount = 0
    var bettingOpen = false
    val results = mutableSetOf<Result>()

    fun connectPlayer(id: Long) {
        playerId = id
    }
}