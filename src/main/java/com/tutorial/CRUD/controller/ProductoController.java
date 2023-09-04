package com.tutorial.CRUD.controller;

import com.tutorial.CRUD.dto.Mensaje;
import com.tutorial.CRUD.dto.ProductoDto;
import com.tutorial.CRUD.entity.Producto;
import com.tutorial.CRUD.service.ProductoService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")

public class ProductoController {
    @Autowired
    ProductoService productoService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public ResponseEntity<List<Producto>> findAll() {
        List<Producto> products = productoService.list();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        if (!productoService.existsByid(id))
            return new ResponseEntity<>(new Mensaje("El producto solicitado no existe"), HttpStatus.NOT_FOUND);
        Producto producto = productoService.getOne(id).get();
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @GetMapping("/detail-name/{name}")
    public ResponseEntity<?> getByNombre(@PathVariable("nombre") String nombre) {
        if (!productoService.existByNombre(nombre))
            return new ResponseEntity<Mensaje>(new Mensaje("El producto con nombre " + nombre + " no existe"), HttpStatus.NOT_FOUND);

        Optional<Producto> optionalProducto = productoService.getByNombre(nombre);

        if(!optionalProducto.isPresent())
            return new ResponseEntity<Mensaje>(new Mensaje("El producto con nombre " + nombre + " no existe"), HttpStatus.NOT_FOUND);

        Producto producto = optionalProducto.get();
        return new ResponseEntity<>(producto, HttpStatus.OK);

    }

    @PostMapping("")
    public ResponseEntity<Mensaje> create(@RequestBody ProductoDto productoDto) {

        if (StringUtils.isBlank(productoDto.getNombre()))
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre del producto es obligatorio"), HttpStatus.BAD_REQUEST);

        if (productoDto.getPrecio() == 0.0 || productoDto.getPrecio() < 0)
            return new ResponseEntity<Mensaje>(new Mensaje("El precio debe ser mayoo que 0.0"), HttpStatus.BAD_REQUEST);

        if (productoService.existByNombre(productoDto.getNombre()))
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre" + productoDto.getNombre() + " ya se encuentra registrado"), HttpStatus.BAD_REQUEST);

        Producto producto = new Producto(productoDto.getNombre(), productoDto.getPrecio());
        productoService.save(producto);
        return new ResponseEntity<Mensaje>(new Mensaje("Producto creado con éxito"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mensaje> update(@PathVariable("id") int id, @RequestBody ProductoDto productoDto){

        Optional<Producto> optionalProductoByName = productoService.getByNombre(productoDto.getNombre());

        if (!productoService.existsByid(id))
            return new ResponseEntity<Mensaje>(new Mensaje("El producto no existe"), HttpStatus.NOT_FOUND);

        if(productoService.existByNombre(productoDto.getNombre()) && optionalProductoByName.isPresent() && productoService.getByNombre(productoDto.getNombre()).get().getId() != id)
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre " + productoDto.getNombre() + " ya se encuentra registrado"), HttpStatus.BAD_REQUEST);

        if (StringUtils.isBlank(productoDto.getNombre()))
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre del producto es obligatorio"), HttpStatus.BAD_REQUEST);

        if (productoDto.getPrecio() == 0.0 || productoDto.getPrecio() < 0)
            return new ResponseEntity<Mensaje>(new Mensaje("El precio deber ser mayor a 0.0"), HttpStatus.BAD_REQUEST);

        Optional<Producto> optionalProducto = productoService.getOne(id);

        if (!optionalProducto.isPresent())
            return new ResponseEntity<Mensaje>(new Mensaje("Error: producto no existe"), HttpStatus.BAD_REQUEST);

        Producto producto = productoService.getOne(id).get();
        producto.setNombre(productoDto.getNombre());
        producto.setPrecio(productoDto.getPrecio());
        productoService.save(producto);
        return new ResponseEntity<Mensaje>(new Mensaje("Producto actualizado con éxito"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable("id") int id){
        if (!productoService.existsByid(id))
            return new ResponseEntity<Mensaje>(new Mensaje("El producto a eliminar no existe"), HttpStatus.NOT_FOUND);

        productoService.delete(id);
        return new ResponseEntity<Mensaje>(new Mensaje("Producto eliminado"), HttpStatus.OK);
    }


}
