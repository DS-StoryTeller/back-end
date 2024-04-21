package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.RefreshEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefreshRedisRepositoryTest {

    @Autowired
    private RefreshRedisRepository refreshRedisRepository;

    @Test
    void testSaveAndFind() {
        // given
        RefreshEntity refreshEntity = new RefreshEntity("refresh", "username");

        // when
        RefreshEntity savedRefresh = refreshRedisRepository.save(refreshEntity);
        String refresh = savedRefresh.getRefresh();
        Optional<RefreshEntity> foundRefresh = refreshRedisRepository.findById(refresh);

        System.out.println(foundRefresh.get().getRefresh());
        System.out.println(foundRefresh.get().getUsername());

        // then
        assertNotNull(foundRefresh);
        assertEquals("username", foundRefresh.get().getUsername());
        assertEquals("refresh", foundRefresh.get().getRefresh());

        // 관련한 테스트용 데이터 삭제
        refreshRedisRepository.delete(savedRefresh);
    }

    @Test
    void testDelete() {
        // given
        RefreshEntity refreshEntity = new RefreshEntity("refresh", "username");
        RefreshEntity savedRefresh = refreshRedisRepository.save(refreshEntity);
        String refresh = savedRefresh.getRefresh();
        System.out.println(refresh);

        // when
        refreshRedisRepository.deleteById(refresh);

        // then
        assertNull(refreshRedisRepository.findById(refresh));
    }

}