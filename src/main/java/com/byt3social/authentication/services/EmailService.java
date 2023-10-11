package com.byt3social.authentication.services;

import com.byt3social.authentication.dto.OrganizacaoDTO;
import com.byt3social.authentication.exceptions.FailedToDeliverEmailException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("classpath:templates/email/documentacao.html")
    private Resource resource;
    @Value("${com.byt3social.app.domain}")
    private String appDomain;

    public void notificarOrganizacao(OrganizacaoDTO organizacaoDTO, String senha) {
        MimeMessage message = mailSender.createMimeMessage();
        String processoLink = appDomain + "/compliance/organizacoes";

        try {
            message.setFrom(new InternetAddress("byt3social@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, organizacaoDTO.email());
            message.setSubject("B3 Social | Envie sua documentação");

            InputStream inputStream = resource.getInputStream();
            String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            htmlTemplate = htmlTemplate.replace("${nome_empresa}", organizacaoDTO.nomeEmpresarial());
            htmlTemplate = htmlTemplate.replace("${empresa_url}", processoLink);
            htmlTemplate = htmlTemplate.replace("${empresa_senha}", senha);

            message.setContent(htmlTemplate, "text/html; charset=utf-8");

            mailSender.send(message);
        } catch (Exception e) {
            throw new FailedToDeliverEmailException();
        }
    }
}
