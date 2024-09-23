package com.cojac.storyteller.repository.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchProfileDelete {

    private final JdbcTemplate jdbcTemplate;
    private final BatchBookDelete batchBookDelete;

    @Transactional
    public void deleteByProfileId(Integer profileId) {

        // 책 ID 목록 조회
        List<Integer> bookIds = jdbcTemplate.queryForList("SELECT id FROM bookEntity WHERE profile_id = ?", Integer.class, profileId);

        // 각 책에 대한 삭제 로직 호출
        for (Integer bookId : bookIds) {
            batchBookDelete.deleteByBookId(bookId);
        }

        // 프로필 삭제
        jdbcTemplate.update("DELETE FROM profileEntity WHERE id = ?", profileId);
    }
}

