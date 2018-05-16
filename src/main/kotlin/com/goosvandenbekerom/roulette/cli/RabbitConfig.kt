package com.goosvandenbekerom.roulette.cli

import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.HeadersExchange
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    companion object {
        const val topicExchangeName = "roulette-dealer-exchange"
        const val fanoutExchangeName = "roulette-update-exchange"
        const val headersExchangeName= "roulette-header-exchange"
        const val DEALER_ROUTING_KEY = "dealer"
    }

    @Bean
    fun topicExchange() = TopicExchange(topicExchangeName)

    @Bean
    fun fanoutExchange() = FanoutExchange(fanoutExchangeName)

    @Bean
    fun headersExchange() = HeadersExchange(headersExchangeName)

    @Bean
    fun protoMessageConverter() = ProtoMessageConverter()

    @Bean
    fun rabbitTemplate(cf: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(cf)
        template.messageConverter = protoMessageConverter()
        return template
    }

    @Bean
    fun listenerFactory(cf: ConnectionFactory, configurer: SimpleRabbitListenerContainerFactoryConfigurer): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        configurer.configure(factory, cf)
        factory.setMessageConverter(protoMessageConverter())
        return factory
    }
}