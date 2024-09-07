package com.cojac.storyteller.repository.queryDSL;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.domain.QBookEntity;
import com.cojac.storyteller.domain.QSettingEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BookEntity> findFavoriteBooksByProfile(ProfileEntity profile) {
        QBookEntity book = QBookEntity.bookEntity;
        QSettingEntity setting = QSettingEntity.settingEntity;

        return queryFactory
                .selectFrom(book)
                .leftJoin(book.setting, setting).fetchJoin()  // Setting 엔티티와 fetch join
                .where(book.profile.eq(profile)
                        .and(book.isFavorite.isTrue()))
                .fetch();
    }
}
