package com.fatec.runetasks.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

/**
 * Utilitário para enviar e-mails HTML usando o Thymeleaf como mecanismo de
 * template.
 * <p>
 * Ele é responsável por processar templates Thymeleaf e enviar e-mails
 * formatados como HTML.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Component
@RequiredArgsConstructor
public class EmailHelper {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    /**
     * Envia um e-mail HTML para um destinatário específico usando um template
     * Thymeleaf.
     * <p>
     * O método processa o template Thymeleaf com os dados fornecidos no contexto e
     * envia o e-mail formatado como HTML para o destinatário especificado.
     * <p>
     * 
     * @param to           endereço de e-mail do destinatário
     * @param subject      assunto do e-mail
     * @param templateName nome do template Thymeleaf
     * @param context      contexto com os dados para o template
     * @throws RuntimeException Caso ocorra um erro ao processar o e-mail HTML
     */
    public void sendHtmlMessage(String to, String subject, String templateName, Context context) {
        try {
            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@runetasks.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao processar e-mail HTML", e);
        }
    }

}
