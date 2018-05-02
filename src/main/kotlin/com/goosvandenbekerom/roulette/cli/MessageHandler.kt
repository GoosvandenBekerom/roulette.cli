package com.goosvandenbekerom.roulette.cli

import com.google.protobuf.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageHandler {
    /*@RabbitListener(queues = ["To be implemented"], containerFactory = "listenerFactory")
    fun receiveMessage(message: Message) {
        TODO("Implement message receiver")
    }*/
}