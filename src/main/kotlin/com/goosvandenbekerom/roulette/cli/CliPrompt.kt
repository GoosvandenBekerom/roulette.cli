package com.goosvandenbekerom.roulette.cli

import org.jline.utils.AttributedString
import org.springframework.shell.jline.PromptProvider
import org.springframework.stereotype.Component

@Component
class CliPrompt : PromptProvider {
    override fun getPrompt() = AttributedString("$ roulette: ")
}