package com.cojac.storyteller.page.repository.batch;

import com.cojac.storyteller.page.entity.PageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BatchPageInsert {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertPages(List<PageEntity> pages) {
        String sql = "INSERT INTO PageEntity (book_id, content, image, pageNumber) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PageEntity page = pages.get(i);
                ps.setInt(1, page.getBook().getId());
                ps.setString(2, page.getContent());
                ps.setString(3, page.getImage());
                ps.setInt(4, page.getPageNumber());
            }

            @Override
            public int getBatchSize() {
                return pages.size();
            }
        });
    }
}
