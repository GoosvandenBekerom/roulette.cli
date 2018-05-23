package com.goosvandenbekerom.roulette.cli

import com.google.protobuf.Message
import org.springframework.stereotype.Component

@Component
class MessageHandler {
    fun handleMessage(message: Message) {
        println(message::class)
        // TODO: actually handle message
    }
}