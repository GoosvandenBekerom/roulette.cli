package com.goosvandenbekerom.roulette.cli

import com.goosvandenbekerom.roulette.core.BetType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import kotlin.reflect.full.createInstance

@Component
class BetTypeConverter : Converter<String, BetType> {
    override fun convert(source: String): BetType {
        return BetType::class.nestedClasses.filter { it.simpleName.equals(source) }.first().createInstance() as BetType
    }
}