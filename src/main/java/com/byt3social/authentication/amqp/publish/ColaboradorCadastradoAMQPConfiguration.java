package com.byt3social.authentication.amqp.publish;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ColaboradorCadastradoAMQPConfiguration {
    @Bean
    public FanoutExchange autenticacaoFanoutExchange() {
        return new FanoutExchange("autenticacao.ex");
    }
}
