package com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.model.Orden;
import com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.repository.OrdenRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;

    public OrdenServiceImpl(OrdenRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orden> findAll() {
        return ordenRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Orden> findById(Long id) {
        return ordenRepository.findById(id);
    }

    @Override
    @Transactional
    public Orden save(Orden orden) {
        validarDatos(orden);
        return ordenRepository.save(orden);
    }

    @Override
    @Transactional
    public Orden update(Long id, Orden orden) {

        validarDatos(orden);

        Orden existente = ordenRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Orden no encontrada con ID: " + id
            ));

        existente.setCompradorId(orden.getCompradorId());
        existente.setEventoId(orden.getEventoId());
        existente.setCantidad(orden.getCantidad());
        existente.setEstado(orden.getEstado());

        return ordenRepository.save(existente);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ordenRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return ordenRepository.existsById(id);
    }


    private void validarDatos(Orden orden) {

        if (orden == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Debes ingresar los datos de la orden"
            );
        }

        if (orden.getCompradorId() == null ||
            orden.getCompradorId() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El ID del comprador debe ser mayor que cero"
            );
        }

        if (orden.getEventoId() == null ||
            orden.getEventoId() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El ID del evento debe ser mayor que cero"
            );
        }

        if (orden.getCantidad() == null ||
            orden.getCantidad() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La cantidad debe ser mayor que cero"
            );
        }
    }

}
