package com.tutorial.CRUD.security.service;

import com.tutorial.CRUD.security.entity.Usuario;
import com.tutorial.CRUD.security.entity.UsuarioPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOptional = usuarioService.getByNombreUsuario(nombreUsuario);
        Usuario usuario = usuarioService.getByNombreUsuario(nombreUsuario).get();

        if (!usuarioOptional.isPresent())
            throw new UsernameNotFoundException("Usuarion no encontrado con nombreUsuario: " + nombreUsuario);

        return UsuarioPrincipal.build(usuario);
    }
}
