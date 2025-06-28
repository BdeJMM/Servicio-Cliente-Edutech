package com.edutech.servicioalcliente.repository;

import com.edutech.servicioalcliente.model.EdutechModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EdutechRepository extends JpaRepository<EdutechModel, Long> {
    List<EdutechModel> findByClienteid(Long clienteId);
    List<EdutechModel> findByEstado(String estado);
}
