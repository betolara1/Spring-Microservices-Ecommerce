package com.betolara1.order.config;

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
    // Chaves que recebe do payments (retorna se esta ok ou deu erro ou cancelou)
    private static final String PAYMENT_OK_QUEUE = "payment.ok";
    private static final String PAYMENT_ERROR_QUEUE = "payment.error";
    private static final String PAYMENT_CANCEL_QUEUE = "payment.cancel";
    private static final String PAYMENT_PROCESSING_QUEUE = "payment.processing";

    // 2. A Fila (A caixa de correio do Payments)
    @Bean
    public Queue paymentOkQueue() {
        return new Queue(PAYMENT_OK_QUEUE);
    }

    @Bean
    public Queue paymentErrorQueue() {
        return new Queue(PAYMENT_ERROR_QUEUE);
    }

    @Bean
    public Queue paymentCancelQueue() {
        return new Queue(PAYMENT_CANCEL_QUEUE);
    }

    @Bean
    public Queue paymentProcessingQueue() {
        return new Queue(PAYMENT_PROCESSING_QUEUE);
    }

    // 3. A Exchange (A agência dos Correios)
    @Bean
    public TopicExchange ecommerceExchange() {
        return new TopicExchange("ecommerce.exchange");
    }

    // 4. A Ligação (Avisando a Agência que a Etiqueta vai praquela Caixa)
    @Bean
    public Binding bindingOk(Queue paymentOkQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentOkQueue).to(ecommerceExchange).with("payment.ok");
    }

    @Bean
    public Binding bindingError(Queue paymentErrorQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentErrorQueue).to(ecommerceExchange).with("payment.error");
    }

    @Bean
    public Binding bindingCancel(Queue paymentCancelQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentCancelQueue).to(ecommerceExchange).with("payment.cancel");
    }

    @Bean
    public Binding bindingProcessing(Queue paymentProcessingQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentProcessingQueue).to(ecommerceExchange).with("payment.processing");
    }

    // Converte Java Objects para JSON quando manda pra fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
