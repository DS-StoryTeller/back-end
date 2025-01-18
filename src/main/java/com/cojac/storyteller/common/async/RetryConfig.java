package com.cojac.storyteller.common.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSendException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;

@Configuration
@EnableRetry
public class RetryConfig {

    @Bean
    public RetryTemplate mailRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        MailSendRetryPolicy mailSendRetryPolicy = new MailSendRetryPolicy(
                3, // 최대 재시도 횟수
                Collections.singletonMap(MailSendException.class, true) // 재시도할 예외
        );

        retryTemplate.setRetryPolicy(mailSendRetryPolicy); // 커스텀 RetryPolicy 적용
        return retryTemplate;
    }

    @Bean(name = "mailRetryInterceptor")
    public RetryOperationsInterceptor retryOperationsInterceptor() {
        RetryOperationsInterceptor interceptor = new RetryOperationsInterceptor();
        interceptor.setRetryOperations(mailRetryTemplate());
        return interceptor;
    }
}
