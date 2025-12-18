package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.Entity.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo,Long> {

}
