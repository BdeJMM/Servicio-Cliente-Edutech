package com.edutech.servicioalcliente.service;

import com.edutech.servicioalcliente.model.EdutechModel;
import java.util.List;
import java.util.Optional;

public interface EdutechService {
    // CRUD básico
    List<EdutechModel> obtenerTodos();
    Optional<EdutechModel> obtenerPorId(Long id);
    EdutechModel guardar(EdutechModel ticket);
    void eliminar(Long id);
    
    // Métodos útiles para servicio al cliente
    List<EdutechModel> obtenerTicketsPorCliente(Long clienteId);
    List<EdutechModel> obtenerTicketsPorEstado(String estado);
}
