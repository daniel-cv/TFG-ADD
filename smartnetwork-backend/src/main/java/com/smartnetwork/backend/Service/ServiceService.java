package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public com.smartnetwork.backend.domain.Entity.Service save(com.smartnetwork.backend.domain.Entity.Service service){
        return serviceRepository.save(service);
    }

    public com.smartnetwork.backend.domain.Entity.Service update(com.smartnetwork.backend.domain.Entity.Service service){
        return serviceRepository.save(service);
    }

    public List<com.smartnetwork.backend.domain.Entity.Service> findAll(){
        return serviceRepository.findAll();
    }

    public Optional<com.smartnetwork.backend.domain.Entity.Service> findById(Long id){
        return serviceRepository.findById(id);
    }
}
