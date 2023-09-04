package com.tutorial.CRUD.security.repository;

import com.tutorial.CRUD.security.entity.Rol;
import com.tutorial.CRUD.security.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer>{
    Optional<Rol> findByRolNombre(RolNombre rolNombre);
}
