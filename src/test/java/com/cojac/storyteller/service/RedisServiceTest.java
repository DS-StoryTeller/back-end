package com.cojac.storyteller.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    @DisplayName("키-값 쌍을 Redis에 저장")
    void testSetValues() {
        // when
        redisService.setValues("testKey", "testValue");

        // then
        verify(valueOperations).set("testKey", "testValue");
    }

    @Test
    @DisplayName("지정된 기간 동안 키-값 쌍을 Redis에 저장")
    void testSetValuesWithDuration() {
        // when
        redisService.setValues("testKey", "testValue", Duration.ofMinutes(5));

        // then
        verify(valueOperations).set("testKey", "testValue", Duration.ofMinutes(5));
    }

    @Test
    @DisplayName("Redis에서 키에 해당하는 값을 가져옴")
    void testGetValues() {
        // given
        when(valueOperations.get("testKey")).thenReturn("testValue");

        // when
        String result = redisService.getValues("testKey");

        // then
        assertEquals("testValue", result);
    }

    @Test
    @DisplayName("Redis에서 키가 없을 때 false 반환")
    void testGetValuesNotExists() {
        // given
        when(valueOperations.get("nonExistingKey")).thenReturn(null);

        // when
        String result = redisService.getValues("nonExistingKey");

        // then
        assertEquals("false", result);
    }

    @Test
    @DisplayName("Redis에서 키-값 쌍을 삭제")
    void testDeleteValues() {
        // when
        redisService.deleteValues("testKey");

        // then
        verify(redisTemplate).delete("testKey");
    }

    @Test
    @DisplayName("키의 유효 기간을 설정")
    void testExpireValues() {
        // when
        redisService.expireValues("testKey", 1000);

        // then
        verify(redisTemplate).expire("testKey", 1000, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("해시 데이터를 Redis에 저장")
    void testSetHashOps() {
        // given
        Map<String, String> hashData = Collections.singletonMap("field", "value");

        // when
        redisService.setHashOps("hashKey", hashData);

        // then
        verify(hashOperations).putAll("hashKey", hashData);
    }

    @Test
    @DisplayName("Redis에서 해시 값을 가져옴")
    void testGetHashOps() {
        // given
        when(hashOperations.hasKey("hashKey", "field")).thenReturn(true);
        when(hashOperations.get("hashKey", "field")).thenReturn("value");

        // when
        String result = redisService.getHashOps("hashKey", "field");

        // then
        assertEquals("value", result);
    }

    @Test
    @DisplayName("해시 필드 키 삭제")
    void testDeleteHashOps() {
        // when
        redisService.deleteHashOps("hashKey", "field");

        // then
        verify(hashOperations).delete("hashKey", "field");
    }

    @Test
    @DisplayName("값이 존재하는지 확인")
    void testCheckExistsValue() {
        // when
        boolean result = redisService.checkExistsValue("someValue");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("값이 존재하지 않을 때 확인")
    void testCheckExistsValue_NotExists() {
        // when
        boolean result = redisService.checkExistsValue("false");

        // then
        assertFalse(result);
    }
}
