package com.goosvandenbekerom.roulette.cli

import com.goosvandenbekerom.roulette.core.Result
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired

class ClientState {
    var playerId: Long = -1
    var chipAmount = 0
    var bettingOpen = false
    val results = mutableSetOf<Result>()

    @Autowired private lateinit var rabbit: RabbitAdmin
    @Autowired private lateinit var exchange: TopicExchange
    @Autowired private lateinit var queue: Queue

    fun connectPlayer(id: Long) {
        playerId = id
        rabbit.declareBinding(BindingBuilder.bind(queue).to(exchange).with("player.$playerId"))
        rabbit.initialize()
    }
}