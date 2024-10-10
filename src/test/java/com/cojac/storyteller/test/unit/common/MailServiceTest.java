package com.cojac.storyteller.test.unit.common;

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
 * 단위 테스트
 *
 * 개별 메서드 및 클래스의 동작을 검증하기 위한 테스트 클래스입니다.
 * 각 테스트는 특정 기능이나 비즈니스 로직을 독립적으로 확인하며,
 * 외부 의존성을 최소화하기 위해 모의 객체를 사용합니다.
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
