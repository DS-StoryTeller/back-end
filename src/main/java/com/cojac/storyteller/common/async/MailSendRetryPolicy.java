package com.cojac.storyteller.common.async;

import org.springframework.mail.MailSendException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.List;
import java.util.Map;

/**
 * 메일 발송 시 재시도 정책을 정의하는 클래스.
 */
public class MailSendRetryPolicy extends SimpleRetryPolicy {

    // 재시도 불가능한 메시지 목록
    private static List<String> nonRetryableMessages = List.of(
            "Failed to close server connection after message sending", // 메시지 발송 후 에러
            "SMTP can only send RFC822 messages",                     // RFC822 메시지 관련 에러
            "is not an InternetAddress",                              // 잘못된 주소 형식
            "No recipient addresses"                                  // 수신인 없음
    );

    public MailSendRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
        super(maxAttempts, retryableExceptions);
    }

    /**
     * 재시도 여부를 결정하는 메서드.
     * @param context 현재 재시도 컨텍스트
     * @return 재시도 가능 여부
     */
    @Override
    public boolean canRetry(RetryContext context) {
        Throwable lastException = context.getLastThrowable();

        // MailSendException 처리
        if ((lastException instanceof MailSendException) && isNonRetryableMessage(lastException)) {
            return false;
        }

        // 기본 정책에 따라 재시도 여부 결정
        return super.canRetry(context);
    }

    /**
     * 예외 메시지가 재시도 불가능한 메시지에 포함되어 있는지 확인.
     * @param exception 발생한 예외
     * @return 재시도 불가능 여부
     */
    private boolean isNonRetryableMessage(Throwable exception) {
        String message = exception.getMessage();
        if (message == null) {
            return false;
        }

        return nonRetryableMessages.stream().anyMatch(message::contains);
    }
}
