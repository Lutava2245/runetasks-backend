package com.fatec.runetasks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuração de envio de emails.
 * <p>
 * Esta classe é responsável por configurar o serviço de envio de emails da
 * aplicação, utilizando as propriedades definidas no arquivo de configuração
 * para autenticação e conexão com o servidor SMTP. Ela cria um bean do tipo
 * {@link JavaMailSender} que pode ser injetado em outras partes da aplicação
 * para enviar emails de forma simples e eficiente.
 * <p>
 * 
 * @author Luan T. Felix
 */
@Configuration
public class EmailConfig {

    /**
     * O nome de usuário (endereço de email) usado para autenticação no servidor
     * SMTP.
     * <p>
     * Ele é lido do arquivo de configuração da aplicação (application.properties) e
     * deve ser um endereço de email válido que tenha permissão para enviar emails
     * através do servidor SMTP configurado.
     * <p>
     */
    @Value("${spring.mail.username}")
    private String username;

    /**
     * A senha usada para autenticação no servidor SMTP.
     * <p>
     * Ela é lida do arquivo de configuração da aplicação (application.properties) e
     * deve ser a senha correspondente ao endereço de email configurado como nome de
     * usuário.
     * <p>
     */
    @Value("${spring.mail.password}")
    private String password;

    /**
     * O host do servidor SMTP.
     * <p>
     * Ele é lido do arquivo de configuração da aplicação (application.properties) e
     * deve ser o endereço do servidor SMTP que será utilizado para envio de emails.
     * <p>
     */
    @Value("${spring.mail.host}")
    private String host;

    /**
     * Configura o JavaMailSender para a aplicação, definindo as propriedades de
     * conexão com o servidor SMTP e autenticação.
     * 
     * @return um objeto {@link JavaMailSender} configurado para a aplicação
     */
    @Bean
    JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(587);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        return mailSender;
    }

}
