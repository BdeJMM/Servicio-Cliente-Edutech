package com.edutech.servicioalcliente.repository;

import com.edutech.servicioalcliente.model.edutechmodel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface edutechrepository extends JpaRepository<edutechmodel, Long> {
    List<edutechmodel> findByClienteid(Long clienteId);
    List<edutechmodel> findByEstado(String estado);
}
