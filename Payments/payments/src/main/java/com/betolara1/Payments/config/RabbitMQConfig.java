package com.betolara1.payments.config;

import org.springframework.amqp.core.Binding;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 1. A Chave de Roteamento (A "Etiqueta" do pacote)
    // Chaves que recebe do order (se foi criado)
    private static final String PAYMENT_CREATED_QUEUE = "payment.created";
    private static final String PAYMENT_REFUND_QUEUE = "payment.refund";

    // 2. A Fila (A caixa de correio do Payments)
    @Bean
    public Queue paymentCreatedQueue() {
        return new Queue(PAYMENT_CREATED_QUEUE);
    }

    @Bean
    public Queue paymentRefundQueue() {
        return new Queue(PAYMENT_REFUND_QUEUE);
    }

    // 3. A Exchange (A agência dos Correios)
    @Bean
    public TopicExchange ecommerceExchange() {
        return new TopicExchange("ecommerce.exchange");
    }

    // 4. A Ligação (Avisando a Agência que a Etiqueta vai praquela Caixa)
    @Bean
    public Binding bindingCreated(Queue paymentCreatedQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentCreatedQueue).to(ecommerceExchange).with("payment.created");
    }

    @Bean
    public Binding bindingRefund(Queue paymentRefundQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentRefundQueue).to(ecommerceExchange).with("payment.refund");
    }

    // Converte Java Objects para JSON quando manda pra fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
