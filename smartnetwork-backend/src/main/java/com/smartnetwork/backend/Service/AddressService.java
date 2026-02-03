package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.Repository.AddressRepository;
import com.smartnetwork.backend.domain.Entity.Address;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address save(Address address){
        return addressRepository.save(address);
    }

    public List<Address> findAll(){
        return addressRepository.findAll();
    }

    public void delete(Address address){
        addressRepository.delete(address);
    }

    public Optional<Address> findById(Long id){
        return addressRepository.findById(id);
    }

    public Address update(Address address){
        return addressRepository.save(address);
    }
}
