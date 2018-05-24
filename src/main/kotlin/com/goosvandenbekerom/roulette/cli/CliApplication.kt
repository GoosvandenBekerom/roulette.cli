package com.goosvandenbekerom.roulette.cli

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class CliApplication {
    @Bean
    fun state() = ClientState()
}

fun main(args: Array<String>) {
    runApplication<CliApplication>(*args)
}