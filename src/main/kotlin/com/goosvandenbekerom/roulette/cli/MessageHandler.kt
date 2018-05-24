package com.goosvandenbekerom.roulette.cli

import com.google.protobuf.Message
import com.goosvandenbekerom.roulette.core.Player
import com.goosvandenbekerom.roulette.core.Result
import com.goosvandenbekerom.roulette.core.RouletteColor
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.Error

@Component
class MessageHandler {
    @Autowired lateinit var state: ClientState

    fun handleMessage(message: Message) {
        when(message) {
            is UpdateBettingStatus -> handleBettingStatusUpdate(message)
            is NewResult -> handleNewResult(message)
            is PlayerAmountUpdate -> handlePlayerAmountUpdate(message)
            is Error -> handleError(message)
            else -> println("Received currently unhandled message of type: ${message::class.simpleName}")
        }
    }

    private fun handleBettingStatusUpdate(message: UpdateBettingStatus) {
        state.bettingOpen = message.status
        println("Betting is now ${if (message.status) "opened" else "closed"}")
    }

    private fun handleNewResult(message: NewResult) {
        val result = Result(message.number, RouletteColor.valueOf(message.color))
        state.results.add(result)
        println("Received new result $result. amount of results: ${state.results.size}")
    }

    private fun handlePlayerAmountUpdate(message: PlayerAmountUpdate) {
        state.chipAmount = message.amount
        println("Received chips update. new amount: ${state.chipAmount}")
    }

    private fun handleError(error: Error) {
        println("Error: ${error.message}")
    }
}