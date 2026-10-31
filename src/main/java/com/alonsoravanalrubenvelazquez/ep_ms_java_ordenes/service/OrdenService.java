package com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.service;

import java.util.List;
import java.util.Optional;

import com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.model.Orden;

public interface OrdenService {

    List<Orden> findAll();

    Optional<Orden> findById(Long id);

    Orden save(Orden orden);

    Orden update(Long id, Orden orden);

    void deleteById(Long id);

    boolean existsById(Long id);
}
