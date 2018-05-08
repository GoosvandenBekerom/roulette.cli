package com.goosvandenbekerom.roulette.cli

import com.google.protobuf.Message

data class Request(val message: Message, val replyTo: String = "")