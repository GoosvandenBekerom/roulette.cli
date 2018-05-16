package com.goosvandenbekerom.roulette.cli

import com.google.protobuf.Message
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
    @Autowired lateinit var state: ClientState

    @ShellMethod("Register a new player")
    fun newPlayer(@ShellOption("-u", "--username") username: String) {
        val body = NewPlayerRequest.newBuilder()
        body.name = username
        println("Requesting registration for $username...")
        val response = request(body.build()) as NewPlayerResponse

        state.connectPlayer(response.id)

        println("Successfully registered and connected $username with player id: ${state.playerId}")
    }

    @ShellMethod("Buy some chips to bet on games")
    fun buyIn(@ShellOption("-a", "--amount") amount: Int) {
        val body = BuyInRequest.newBuilder()
        body.playerId = state.playerId
        body.amount = amount
        println("Requesting buy-in for $amount chips")
        val response = request(body.build()) as PlayerAmountUpdate

        state.chipAmount = response.amount

        println("Buy-in successful. new chip amount = ${state.chipAmount}")
    }

    @ShellMethod("Bet on a game")
    fun bet( // TODO: FIX CONVERTER FOR NUMBER TYPES (NON NO-ARG CONSTRUCTOR TYPES)
        @ShellOption("-a", "--amount") amount: Int,
        @ShellOption("-t", "--type", valueProvider = BetTypeProvider::class) type: BetType,
        @ShellOption("-n1", "--number1", defaultValue = "-1") n1: Int,
        @ShellOption("-n2", "--number2", defaultValue = "-1") n2: Int,
        @ShellOption("-n3", "--number3", defaultValue = "-1") n3: Int,
        @ShellOption("-n4", "--number4", defaultValue = "-1") n4: Int,
        @ShellOption("-n5", "--number5", defaultValue = "-1") n5: Int,
        @ShellOption("-n6", "--number6", defaultValue = "-1") n6: Int
    ){
        if (amount > state.chipAmount){
            println("You don't have enough chips to make that bet. please buy-in first")
            return
        }

        val body = BetRequest.newBuilder()
        body.playerId = state.playerId
        body.amount = amount
        body.type = betTypeToProto(type)
        val numbers = setOf(n1, n2, n3, n4, n5, n6).filter { it in 0..37 }
        if (numbers.isNotEmpty()) {
            body.addAllNumber(numbers)
        }
        println("Betting $amount on ${type::class.simpleName}")
        val response = request(body.build()) as PlayerAmountUpdate

        state.chipAmount = response.amount

        println("Bet successful. new chip amount = ${state.chipAmount}")
    }

    // ======================
    // Availability functions
    // ======================

    fun betAvailability() = playerAvailable(true)
    fun buyInAvailability() = playerAvailable()

    fun playerAvailable(shouldHaveChips: Boolean = false): Availability {
        return if (!playerConnected())
            Availability.unavailable("no player is connected/registered, register one with 'new-player [name]'")
        else if (shouldHaveChips && state.chipAmount <= 0)
            Availability.unavailable("you don't own any chips, please buy-in first")
        else
            Availability.available()
    }

    // =================
    // Private functions
    // =================

    private fun request(body: Message) : Message {
        return rabbit.convertSendAndReceive(exchange.name, RabbitConfig.DEALER_ROUTING_KEY, body) as Message
    }

    private fun playerConnected(): Boolean {
        return state.playerId > -1
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