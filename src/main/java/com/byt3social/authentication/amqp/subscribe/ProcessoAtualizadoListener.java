package com.byt3social.authentication.amqp.subscribe;

import com.byt3social.authentication.dto.OrganizacaoDTO;
import com.byt3social.authentication.enums.StatusCadastro;
import com.byt3social.authentication.services.OrganizacaoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ProcessoAtualizadoListener {
    @Autowired
    private OrganizacaoService organizacaoService;

    @RabbitListener(queues = "processo.atualizado.authentication")
    public void processoAtualizado(@Payload OrganizacaoDTO organizacaoDTO) {
        if(organizacaoDTO.statusCadastro() == StatusCadastro.ABERTO) {
            organizacaoService.cadastrarUsuario(organizacaoDTO);
        }
    }
}
