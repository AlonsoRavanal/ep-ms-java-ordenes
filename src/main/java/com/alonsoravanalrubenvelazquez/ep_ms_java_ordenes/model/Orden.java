package com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del comprador es obligatorio")
    @Positive(message = "El ID del comprador debe ser mayor que cero")
    @Column(nullable = false)
    private Long compradorId;

    @NotNull(message = "El ID del evento es obligatorio")
    @Positive(message = "El ID del evento debe ser mayor que cero")
    @Column(nullable = false)
    private Long eventoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que cero")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoOrden.ACEPTADA_PENDIENTE_EMISION;
        }
    }

    public enum EstadoOrden {
        ACEPTADA_PENDIENTE_EMISION,
        EMITIDA,
        RECHAZADA_SIN_CUPO,
        ERROR_EMISION
    }

    public Orden() {
    }

    public Orden(Long compradorId, Long eventoId, Integer cantidad, LocalDateTime fechaCreacion, EstadoOrden estado) {
        this.compradorId = compradorId;
        this.eventoId = eventoId;
        this.cantidad = cantidad;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public Long getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(Long compradorId) {
        this.compradorId = compradorId;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

}
