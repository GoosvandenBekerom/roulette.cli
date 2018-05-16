package com.goosvandenbekerom.roulette.cli

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.HeadersExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ClientState {
    @Autowired lateinit var exchange: HeadersExchange

    var playerId: Long = -1
    var chipAmount = 0
    var bettingOpen = false

    var queue: Queue? = null

    fun connectPlayer(id: Long) {
        playerId = id
        queue = Queue("", false, true, true)
        BindingBuilder.bind(queue).to(exchange).where("player_id").matches(playerId)
    }

//    inner class Listener {
//        @RabbitListener(queues = [queue.name])
//        fun receive() {
//
//        }
//    }
}