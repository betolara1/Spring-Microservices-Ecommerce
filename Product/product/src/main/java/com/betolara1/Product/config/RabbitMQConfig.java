package com.betolara1.product.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 3. A Exchange (A agência dos Correios)
    @Bean
    public TopicExchange ecommerceExchange() {
        return new TopicExchange("ecommerce.exchange");
    }

    // Converte Java Objects para JSON quando manda pra fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
