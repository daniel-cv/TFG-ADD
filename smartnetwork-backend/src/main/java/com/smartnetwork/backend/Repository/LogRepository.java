package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}