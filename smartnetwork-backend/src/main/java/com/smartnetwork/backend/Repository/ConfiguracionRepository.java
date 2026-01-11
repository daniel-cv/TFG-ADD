package com.smartnetwork.backend.Repository;

import com.smartnetwork.backend.domain.Entity.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion,Long> {

}
