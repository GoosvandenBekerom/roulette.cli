package com.goosvandenbekerom.roulette.cli

import com.goosvandenbekerom.roulette.core.BetType
import org.springframework.core.MethodParameter
import org.springframework.shell.CompletionContext
import org.springframework.shell.CompletionProposal
import org.springframework.shell.standard.ValueProvider
import org.springframework.stereotype.Component

// TODO: figure out why this doesnt work??
@Component
class BetTypeProvider : ValueProvider {
    override fun supports(parameter: MethodParameter, completionContext: CompletionContext): Boolean {
        return BetType::class.nestedClasses
                .filter { it.simpleName.equals(parameter.parameterType.simpleName) }
                .any()
    }

    override fun complete(parameter: MethodParameter, completionContext: CompletionContext, hints: Array<out String>): MutableList<CompletionProposal> {
        println("trying to complete")
        return BetType::class.nestedClasses
                .filter { it.simpleName!!.startsWith(completionContext.currentWordUpToCursor()) }
                .map { CompletionProposal(it.simpleName) }
                .toMutableList()
    }
}