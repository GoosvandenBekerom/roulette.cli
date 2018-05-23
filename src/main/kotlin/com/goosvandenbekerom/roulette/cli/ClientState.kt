package com.goosvandenbekerom.roulette.cli

import org.springframework.stereotype.Component

@Component
class ClientState {
    var playerId: Long = -1
    var chipAmount = 0

    fun connectPlayer(id: Long) {
        playerId = id
    }
}