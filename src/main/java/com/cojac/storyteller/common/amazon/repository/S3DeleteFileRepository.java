package com.cojac.storyteller.common.amazon.repository;

import com.cojac.storyteller.common.amazon.domain.S3DeleteFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3DeleteFileRepository extends JpaRepository<S3DeleteFile, Long> {
}
