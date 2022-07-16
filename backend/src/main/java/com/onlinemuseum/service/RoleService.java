package com.onlinemuseum.service;

import com.onlinemuseum.domain.entity.Role;
import com.onlinemuseum.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name){
        return  roleRepository.findAppUserRoleByName(name);
    }
}
