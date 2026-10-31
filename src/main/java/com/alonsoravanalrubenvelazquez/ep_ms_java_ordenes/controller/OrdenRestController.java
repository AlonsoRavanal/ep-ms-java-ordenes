package com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.model.Orden;
import com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.service.OrdenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenRestController {

    private final OrdenService ordenService;

    public OrdenRestController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public ResponseEntity<List<Orden>> listar() {
        return ResponseEntity.ok(ordenService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtener(@PathVariable Long id) {
        return ordenService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Orden> crear(
            @Valid @RequestBody Orden orden) {

        Orden creada = ordenService.save(orden);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Orden> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Orden orden) {

        if (!ordenService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
            ordenService.update(id, orden)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!ordenService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ordenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
