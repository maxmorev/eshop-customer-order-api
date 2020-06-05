package ru.maxmorev.eshop.customer.order.api.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Locale;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ValidationMSConfig {
    private final MessageSource messageSource;

    public MessageSource messageSource() {
        return messageSource;
    }

    @Bean
    @SneakyThrows
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
