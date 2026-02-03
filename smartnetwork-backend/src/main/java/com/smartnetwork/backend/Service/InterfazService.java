package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.InterfazRepository;
import com.smartnetwork.backend.domain.Entity.Interfaz;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InterfazService {
    private final InterfazRepository interfazRepository;

    public InterfazService(InterfazRepository interfazRepository) {
        this.interfazRepository = interfazRepository;
    }

    public Optional<Interfaz> findById(Long id){
        return interfazRepository.findById(id);
    }

    public List<Interfaz> findAll(){
        return interfazRepository.findAll();
    }

    public Interfaz create(Interfaz interfaz){
        return interfazRepository.save(interfaz);
    }

    public Interfaz update(Interfaz interfaz){
        return interfazRepository.save(interfaz);
    }

    public void delete(Interfaz interfaz){
        interfazRepository.delete(interfaz);
    }
}
