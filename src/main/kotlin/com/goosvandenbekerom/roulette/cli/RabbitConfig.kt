package com.goosvandenbekerom.roulette.cli

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
        const val DEALER_ROUTING_KEY = "dealer"
    }

    @Bean
    fun exchange() = TopicExchange(topicExchangeName)

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