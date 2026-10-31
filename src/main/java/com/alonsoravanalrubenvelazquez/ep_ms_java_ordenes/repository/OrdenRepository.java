package com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alonsoravanalrubenvelazquez.ep_ms_java_ordenes.model.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

}
