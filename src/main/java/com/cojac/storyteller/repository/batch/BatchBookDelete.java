package com.cojac.storyteller.repository.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchBookDelete {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void deleteByBookId(Integer bookId) {

        // 페이지 ID 목록 조회
        List<Integer> pageIds = jdbcTemplate.queryForList("SELECT id FROM pageEntity WHERE book_id = ?", Integer.class, bookId);

        // 모르는 단어 삭제
        jdbcTemplate.batchUpdate(
                "DELETE FROM unknownWordEntity WHERE page_id = ?", new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, pageIds.get(i));
                    }
                    @Override
                    public int getBatchSize() {
                        return pageIds.size();
                    }
                }
        );

        // 페이지 삭제
        jdbcTemplate.batchUpdate(
                "DELETE FROM pageEntity WHERE id = ?", new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, pageIds.get(i));
                    }
                    @Override
                    public int getBatchSize() {
                        return pageIds.size();
                    }
                }
        );

        // 설정 조회
        Integer settingId = jdbcTemplate.queryForObject("SELECT setting_id FROM bookEntity WHERE id = ?", Integer.class, bookId);

        // 책 및 설정 삭제
        jdbcTemplate.update("DELETE FROM bookEntity WHERE id = ?", bookId);
        jdbcTemplate.update("DELETE FROM settingEntity WHERE id = ?", settingId);
    }
}

