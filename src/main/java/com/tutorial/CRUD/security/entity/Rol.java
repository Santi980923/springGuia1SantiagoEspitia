package com.tutorial.CRUD.security.entity;

import com.tutorial.CRUD.security.enums.RolNombre;

import lombok.*;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class Rol implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private RolNombre rolNombre;

    public Rol(@NotNull RolNombre rolNombre) {
        this.rolNombre = rolNombre;
    }
}
