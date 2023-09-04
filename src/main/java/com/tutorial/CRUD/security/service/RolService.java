package com.tutorial.CRUD.security.service;

import com.tutorial.CRUD.security.entity.Rol;
import com.tutorial.CRUD.security.enums.RolNombre;
import com.tutorial.CRUD.security.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.util.Optional;

@Service
@Transactional

public class RolService {
    @Autowired
    private RolRepository rolRepository;

    public Optional<Rol> getByRolNombre(RolNombre rolNombre){
        return rolRepository.findByRolNombre(rolNombre);
    }

    public void save(Rol rol){
        rolRepository.save(rol);
    }
}
