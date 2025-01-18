package com.cojac.storyteller.email;

import com.cojac.storyteller.common.async.MailSendRetryPolicy;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MailSendRetryPolicyTest {

    @Test
    @DisplayName("재시도 불가 메시지를 포함한 예외가 발생하면 즉시 재시도 중단")
    void shouldNotRetryForNonRetryableMessage() {
        String message = "No recipient addresses"; // 재시도 불가 메시지
        MailSendRetryPolicy mailSendRetryPolicy = new MailSendRetryPolicy(3,
                Collections.singletonMap(MailSendException.class, true));
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(mailSendRetryPolicy);
        final AtomicInteger tryCount = new AtomicInteger();

        try {
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                log.info("현재 재시도 횟수: {}", context.getRetryCount());
                tryCount.incrementAndGet();
                throw new MailSendException(message);
            });
        } catch (Exception e) {
            log.info("재시도 종료");
        }

        Assertions.assertThat(tryCount.intValue()).isEqualTo(1); // 재시도는 1회로 종료
    }


    @Test
    @DisplayName("재시도 가능한 메시지를 포함하는 예외 발생 시 3회 재시도 수행")
    void shouldRetryThreeTimesForRetryableMessage() {
        String message = "재시도 가능한 메시지"; // 재시도 가능 메시지
        MailSendRetryPolicy mailSendRetryPolicy = new MailSendRetryPolicy(3,
                Collections.singletonMap(MailSendException.class, true));
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(mailSendRetryPolicy);
        final AtomicInteger tryCount = new AtomicInteger();

        try {
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                log.info("현재 재시도 횟수: {}", context.getRetryCount());
                tryCount.incrementAndGet();
                throw new MailSendException(message);
            });
        } catch (Exception e) {
            log.info("재시도 종료");
        }

        Assertions.assertThat(tryCount.intValue()).isEqualTo(3); // 재시도는 3회로 종료
    }
}
