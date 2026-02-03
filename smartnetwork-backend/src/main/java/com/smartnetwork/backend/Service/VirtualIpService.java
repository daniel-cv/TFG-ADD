package com.smartnetwork.backend.Service;

import com.smartnetwork.backend.domain.Entity.VirtualIp;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VirtualIpService {
    private final VirtualIpService virtualIpService;

    public VirtualIpService(VirtualIpService virtualIpService){
        this.virtualIpService = virtualIpService;
    }

    public VirtualIp save(VirtualIp virtualIp){
        return virtualIpService.save(virtualIp);
    }

    public VirtualIp update(VirtualIp virtualIp){
        return virtualIpService.update(virtualIp);
    }

    public VirtualIp delete(VirtualIp virtualIp){
        return virtualIpService.delete(virtualIp);
    }

    public VirtualIp findById(Long id){
        return virtualIpService.findById(id);
    }

    public List<VirtualIp> findAll(){
        return virtualIpService.findAll();
    }
}
