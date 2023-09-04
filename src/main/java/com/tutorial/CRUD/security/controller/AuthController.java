package com.tutorial.CRUD.security.controller;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.tutorial.CRUD.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.tutorial.CRUD.dto.Mensaje;
import com.tutorial.CRUD.security.dto.JwtDto;
import com.tutorial.CRUD.security.dto.NuevoUsuario;
import com.tutorial.CRUD.security.dto.LoginUsuario;
import com.tutorial.CRUD.security.entity.Rol;
import com.tutorial.CRUD.security.entity.Usuario;
import com.tutorial.CRUD.security.enums.RolNombre;
import com.tutorial.CRUD.security.jwt.JwtProvider;
import com.tutorial.CRUD.security.service.RolService;
import com.tutorial.CRUD.security.service.UsuarioService;
import com.tutorial.CRUD.service.ProductoService;
@RestController
@RequestMapping("/auth")
@CrossOrigin

public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("")
    public ResponseEntity<Mensaje> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return new ResponseEntity<Mensaje>(new Mensaje("Verifique los datos introducidos"), HttpStatus.BAD_REQUEST);
        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario()))
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre " + nuevoUsuario.getNombre() + "ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        if (usuarioService.existsByEmail(nuevoUsuario.getEmail()))
            return new ResponseEntity<Mensaje>(new Mensaje("El email " + nuevoUsuario.getEmail() + "ya se encuentra registrado"), HttpStatus.BAD_REQUEST);

        Usuario usuario =
                new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(), nuevoUsuario.getEmail(),
                        passwordEncoder.encode(nuevoUsuario.getPassword()));

        Set<Rol> roles = new HashSet<>();
        Optional<Rol> userRoleNombre = rolService.getByRolNombre(RolNombre.ROLE_USER);
        Optional<Rol> adminRoleNombre = rolService.getByRolNombre(RolNombre.ROLE_ADMIN);
        //roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());

        /*if (nuevoUsuario.getRoles().contains("admin"))
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
        usuario.setRoles(roles);
        usuarioService.save(usuario);
        return new ResponseEntity<Mensaje>(new Mensaje("Usuario registrado con exito"), HttpStatus.CREATED);

         */

        if (!userRoleNombre.isPresent())
            return new ResponseEntity<Mensaje>(new Mensaje("Error: el rol de usuario no se encuentra"), HttpStatus.BAD_REQUEST);
        if (!adminRoleNombre.isPresent())
            return new ResponseEntity<Mensaje>(new Mensaje("Error: el rol de usuario no se encuentra"), HttpStatus.BAD_REQUEST);

        roles.add(userRoleNombre.get());

        if (nuevoUsuario.getRoles().contains("admin"))
            roles.add(adminRoleNombre.get());

        usuario.setRoles(roles);
        usuarioService.save(usuario);

        return new ResponseEntity<Mensaje>(new Mensaje("Usuario Registrado Satisfactoriamente"), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity<Mensaje>(new Mensaje("Error: Usuario Invalido"), HttpStatus.UNAUTHORIZED);

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        JwtDto jwtDto = new JwtDto(jwt);
        return new ResponseEntity<JwtDto>(jwtDto, HttpStatus.ACCEPTED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws ParseException{
        String token = jwtProvider.refreshToken(jwtDto);
        JwtDto jwt = new JwtDto(token);
        return new ResponseEntity<JwtDto>(jwt, HttpStatus.OK);
    }
}
