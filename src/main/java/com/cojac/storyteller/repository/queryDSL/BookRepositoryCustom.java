package com.cojac.storyteller.repository.queryDSL;

import com.cojac.storyteller.domain.BookEntity;
import com.cojac.storyteller.domain.ProfileEntity;
import com.cojac.storyteller.dto.book.BookListResponseDTO;

import java.util.List;

public interface BookRepositoryCustom {

    List<BookEntity> findFavoriteBooksByProfile(ProfileEntity profile);
}
