package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByDispositivoId(Long dispositivoId);
}