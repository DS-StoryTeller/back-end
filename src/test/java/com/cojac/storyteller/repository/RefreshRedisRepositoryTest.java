package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.RefreshEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefreshRedisRepositoryTest {

    @Autowired
    private RefreshRedisRepository refreshRedisRepository;
    private RefreshEntity savedRefresh; // 각 테스트에서 생성된 엔티티를 추적하는 필드

    @Test
    void testSaveAndFind() {
        // given
        RefreshEntity refreshEntity = new RefreshEntity("refresh", "username");

        // when
        savedRefresh = refreshRedisRepository.save(refreshEntity);
        String refresh = savedRefresh.getRefresh();
        Optional<RefreshEntity> foundRefresh = refreshRedisRepository.findById(refresh);

        System.out.println(foundRefresh.get().getRefresh());
        System.out.println(foundRefresh.get().getUsername());

        // then
        assertNotNull(foundRefresh);
        assertEquals("username", foundRefresh.get().getUsername());
        assertEquals("refresh", foundRefresh.get().getRefresh());
    }

    @Test
    void testDelete() {
        // given
        RefreshEntity refreshEntity = new RefreshEntity("refresh", "username");
        savedRefresh = refreshRedisRepository.save(refreshEntity);
        String refresh = savedRefresh.getRefresh();
        System.out.println(refresh);

        // when
        refreshRedisRepository.deleteById(refresh);

        // then
        assertFalse(refreshRedisRepository.findById(refresh).isPresent());
    }

    @AfterEach
    void tearDown() {
        if (savedRefresh != null) {
            refreshRedisRepository.delete(savedRefresh);
        }
        savedRefresh = null; // 필드를 null로 초기화하여 재사용 가능하게 만듭니다.
    }

}