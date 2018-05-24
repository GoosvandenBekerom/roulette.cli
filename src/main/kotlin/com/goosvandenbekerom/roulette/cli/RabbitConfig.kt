package com.goosvandenbekerom.roulette.cli

import org.springframework.amqp.core.*
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class RabbitConfig {
    companion object {
        const val topicExchangeName = "roulette-dealer-exchange"
        const val fanoutExchangeName = "roulette-update-exchange"
        const val DEALER_ROUTING_KEY = "dealer"
    }

    @Bean
    fun topicExchange() = TopicExchange(topicExchangeName)

    @Bean
    fun fanoutExchange() = FanoutExchange(fanoutExchangeName)

    @Bean
    fun queue() = Queue(UUID.randomUUID().toString(), false, true, true)

    @Bean
    fun binding(queue: Queue) = BindingBuilder.bind(queue).to(fanoutExchange())!!

    @Bean
    fun protoMessageConverter() = ProtoMessageConverter()

    @Bean
    fun rabbitAdmin(cf: ConnectionFactory) = RabbitAdmin(cf)

    @Bean
    fun rabbitTemplate(cf: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(cf)
        template.messageConverter = protoMessageConverter()
        return template
    }

    @Bean
    fun container(cf: ConnectionFactory, adapter: MessageListenerAdapter, queue:Queue) : SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.connectionFactory = cf
        container.setQueueNames(queue.name)
        container.messageListener = adapter
        return container
    }

    @Bean
    fun listenerAdapter(handler: MessageHandler) : MessageListenerAdapter = MessageListenerAdapter(handler, protoMessageConverter())
}