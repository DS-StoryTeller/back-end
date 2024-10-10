package com.cojac.storyteller.profile.repository.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchProfileDelete {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void deleteByProfileId(Integer profileId) {

        // 책 ID 목록 조회
        List<Integer> bookIds = jdbcTemplate.queryForList("SELECT id FROM BookEntity WHERE profile_id = ?", Integer.class, profileId);

        if (!bookIds.isEmpty()) {
            // 각 책에 대한 설정 ID를 조회
            List<Integer> settingIds = jdbcTemplate.queryForList("SELECT setting_id FROM BookEntity WHERE id IN (?)", Integer.class, bookIds);

            // 모르는 단어 삭제
            jdbcTemplate.batchUpdate(
                    "DELETE FROM UnknownWordEntity WHERE page_id IN (SELECT id FROM PageEntity WHERE book_id = ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, bookIds.get(i));
                        }

                        @Override
                        public int getBatchSize() {
                            return bookIds.size();
                        }
                    }
            );

            // 페이지 삭제
            jdbcTemplate.batchUpdate(
                    "DELETE FROM PageEntity WHERE book_id = ?",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, bookIds.get(i));
                        }

                        @Override
                        public int getBatchSize() {
                            return bookIds.size();
                        }
                    }
            );

            // 책 삭제
            jdbcTemplate.batchUpdate(
                    "DELETE FROM BookEntity WHERE id = ?",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, bookIds.get(i));
                        }

                        @Override
                        public int getBatchSize() {
                            return bookIds.size();
                        }
                    }
            );

            // 설정 삭제
            jdbcTemplate.batchUpdate(
                    "DELETE FROM SettingEntity WHERE id = ?",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, settingIds.get(i));
                        }

                        @Override
                        public int getBatchSize() {
                            return settingIds.size();
                        }
                    }
            );
        }

        // 프로필 삭제
        jdbcTemplate.update("DELETE FROM ProfileEntity WHERE id = ?", profileId);
    }

}
