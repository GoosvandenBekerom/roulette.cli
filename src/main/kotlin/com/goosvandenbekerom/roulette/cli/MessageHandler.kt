package com.goosvandenbekerom.roulette.cli

import com.google.protobuf.Message
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageHandler {
   /* @RabbitListener(queuesToDeclare = [Queue("")])
    fun responseListener(message: Message) {
        println(message)
    }*/
}