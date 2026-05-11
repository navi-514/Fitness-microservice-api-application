package com.example.activityservice.config;

import org.springframework.amqp.core.*;


import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitmqConfig {

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.exchange.name}")
    public String exchange;

    @Value("${rabbitmq.routing.key}")
    public String routingKey;

    @Bean
    public Queue activityQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange) {
        return  BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
