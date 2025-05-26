package com.edutech.servicioalcliente.service;

import com.edutech.servicioalcliente.model.edutechmodel;
import java.util.List;
import java.util.Optional;

public interface edutechservice {
    // CRUD básico
    List<edutechmodel> obtenerTodos();
    Optional<edutechmodel> obtenerPorId(Long id);
    edutechmodel guardar(edutechmodel ticket);
    void eliminar(Long id);
    
    // Métodos útiles para servicio al cliente
    List<edutechmodel> obtenerTicketsPorCliente(Long clienteId);
    List<edutechmodel> obtenerTicketsPorEstado(String estado);
}
