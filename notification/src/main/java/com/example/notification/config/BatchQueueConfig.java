//package com.example.notification.config;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.converter.MessageConverter;
//
//public class BatchQueueConfig {
//    @Value("${batch-exchange}")
//    private String EXCHANGE_NAME;
//    @Value("${batch-queue}")
//    private String QUEUE_NAME;
//    @Value("${batch-route-key}")
//    private String ROUTING_KEY;
//
//    @Bean
//    TopicExchange exchange(){
//        return new TopicExchange(EXCHANGE_NAME);
//    }
//
//    @Bean
//    Queue queue(){
//        return new Queue(QUEUE_NAME);
//    }
//
//    @Bean
//    Binding binding(Queue queue,TopicExchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
//    }
//
//    @Bean
//    RabbitTemplate rabbitTemplate (ConnectionFactory connectionFactory, MessageConverter messageConverter){
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//        return rabbitTemplate;
//    }
//}
