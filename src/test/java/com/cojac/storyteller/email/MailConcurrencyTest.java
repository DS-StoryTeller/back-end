package com.cojac.storyteller.email;

import com.cojac.storyteller.common.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
public class MailConcurrencyTest {

    @Autowired
    private MailService mailService;

    /**
     * 동시 메일 발송 테스트 - 메일 유실율 테스트
     */
    @Test
    void testSendEmailWithMultipleThreads() throws InterruptedException {
        // 동시에 전송할 이메일 작업 수
        int threadCount = 20;

        // 성공 및 실패 카운터
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        // 동시 작업 처리
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final int emailIndex = i;
            executorService.submit(() -> {
                String toEmail = "test" + emailIndex + "@example.com";
                String title = "Test Email Title" + emailIndex;
                String text = "Test Email Text " + emailIndex;
                try {
                    mailService.sendEmail(toEmail, title, text);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                }
            });
        }

        // Executor 종료 및 모든 작업 완료 대기
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // 결과 출력
        System.out.println("Successful email sends: " + successCount.get());
        System.out.println("Failed email sends: " + failureCount.get());
    }
}
