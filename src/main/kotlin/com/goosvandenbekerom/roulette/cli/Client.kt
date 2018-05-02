package com.goosvandenbekerom.roulette.cli

import com.goosvandenbekerom.roulette.core.BetType
import com.goosvandenbekerom.roulette.proto.RouletteProto.*
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.Availability
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class Client {
    @Autowired lateinit var rabbit: RabbitTemplate
    @Autowired lateinit var exchange: TopicExchange

    private var playerId = -1

    @ShellMethod("Register a new player")
    fun newPlayer(@ShellOption("-u", "--username") username: String) {
        val request = NewPlayerRequest.newBuilder()
        request.name = username
        rabbit.convertAndSend(exchange.name, RabbitConfig.DEALER_ROUTING_KEY, request.build())
        playerId = 1 // TODO: get player id from response
        println("Successfully established connection for $username")
    }

    @ShellMethod("Bet on a game")
    fun bet(
        @ShellOption("-g", "--game") gameId: Long,
        @ShellOption("-a", "--amount") amount: Int,
        @ShellOption("-t", "--type", valueProvider = BetTypeProvider::class) type: BetType,
        @ShellOption("-n1", "--number1", defaultValue = "-1") n1: Int,
        @ShellOption("-n2", "--number2", defaultValue = "-1") n2: Int,
        @ShellOption("-n3", "--number3", defaultValue = "-1") n3: Int,
        @ShellOption("-n4", "--number4", defaultValue = "-1") n4: Int,
        @ShellOption("-n5", "--number5", defaultValue = "-1") n5: Int,
        @ShellOption("-n6", "--number6", defaultValue = "-1") n6: Int
    ){
        val request = BetRequest.newBuilder()
        request.gameId = gameId
        request.amount = amount
        request.type = betTypeToProto(type)
        val numbers = setOf(n1, n2, n3, n4, n5, n6).filter { it in 0..37 }
        if (numbers.isNotEmpty()) {
            request.addAllNumber(numbers)
        }
        rabbit.convertAndSend(exchange.name, RabbitConfig.DEALER_ROUTING_KEY, request.build())
        println("Betting $amount on ${type::class.simpleName} in game $gameId")
    }

    // ======================
    // Availability functions
    // ======================

    fun betAvailability(): Availability{
        return if (playerConnected()) Availability.available()
        else Availability.unavailable("no player is connected/registered")
    }

    // =================
    // Private functions
    // =================

    private fun playerConnected(): Boolean {
        return playerId > -1
    }

    private fun betTypeToProto(type: BetType): BetRequest.BetType {
        return when (type){
            is BetType.Odd -> BetRequest.BetType.ODD
            is BetType.Even -> BetRequest.BetType.EVEN
            is BetType.Red -> BetRequest.BetType.RED
            is BetType.Black -> BetRequest.BetType.BLACK
            is BetType.FirstHalf -> BetRequest.BetType.FIRST_HALf
            is BetType.SecondHalf -> BetRequest.BetType.SECOND_HALF
            is BetType.FirstDozen -> BetRequest.BetType.FIRST_DOZEN
            is BetType.SecondDozen -> BetRequest.BetType.SECOND_DOZEN
            is BetType.ThirdDozen -> BetRequest.BetType.THIRD_DOZEN
            is BetType.FirstColumn -> BetRequest.BetType.FIRST_COLUMN
            is BetType.SecondColumn -> BetRequest.BetType.SECOND_COLUMN
            is BetType.ThirdColumn -> BetRequest.BetType.THIRD_COLUMN
            is BetType.Number -> BetRequest.BetType.NUMBER
            is BetType.TwoNumber -> BetRequest.BetType.TWO_NUMBER
            is BetType.ThreeNumber -> BetRequest.BetType.THREE_NUMBER
            is BetType.FourNumber -> BetRequest.BetType.FOUR_NUMBER
            is BetType.FiveNumber -> BetRequest.BetType.FIVE_NUMBER
            is BetType.SixNumber -> BetRequest.BetType.SIX_NUMBER
        }
    }
}