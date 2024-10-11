package com.cojac.storyteller.unit.common;

import com.cojac.storyteller.common.mail.MailService;
import com.cojac.storyteller.user.exception.EmailSendingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * 단위 테스트 클래스
 *
 * 이 클래스는 개별 단위(주로 서비스 또는 비즈니스 로직) 기능을 검증하기 위한 단위 테스트를 포함합니다.
 *
 * 주요 특징:
 * - 모의 객체(mock objects)를 사용하여 외부 의존성을 제거하고,테스트 대상 객체의 로직에만 집중합니다.
 * - 테스트의 독립성을 보장하여, 각 테스트가 서로에게 영향을 미치지 않도록 합니다.
 *
 * 테스트 전략:
 * - 간단한 기능이나 로직에 대한 테스트는 단위 테스트를 사용하십시오.
 * - 시스템의 전체적인 동작 및 상호작용을 검증하기 위해 통합 테스트를 활용하십시오.
 *
 * 참고: 단위 테스트는 실행 속도가 빠르며,
 *       전체 시스템의 동작보다는 개별 단위의 동작을 검증하는 데 중점을 둡니다.
 */
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("이메일 전송 성공")
    void testSendEmail_Success() {
        // given
        String toEmail = "test@example.com";
        String title = "Test Title";
        String text = "Test Email Content";

        // when
        mailService.sendEmail(toEmail, title, text);

        // then
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(toEmail);
        expectedMessage.setSubject(title);
        expectedMessage.setText(text);

        verify(emailSender, times(1)).send(expectedMessage);
    }

    @Test
    @DisplayName("이메일 전송 실패 시 예외 발생")
    void testSendEmail_Failure() {
        // given
        String toEmail = "test@example.com";
        String title = "Test Title";
        String text = "Test Email Content";

        doThrow(new RuntimeException("Email sending failed")).when(emailSender).send(any(SimpleMailMessage.class));

        // when & then
        assertThrows(EmailSendingException.class, () -> mailService.sendEmail(toEmail, title, text));
    }
}
