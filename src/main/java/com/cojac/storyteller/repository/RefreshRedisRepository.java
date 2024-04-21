package com.cojac.storyteller.repository;

import com.cojac.storyteller.domain.RefreshEntity;
import org.springframework.data.repository.CrudRepository;

public interface RefreshRedisRepository extends CrudRepository<RefreshEntity, String> {

}
