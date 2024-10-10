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
