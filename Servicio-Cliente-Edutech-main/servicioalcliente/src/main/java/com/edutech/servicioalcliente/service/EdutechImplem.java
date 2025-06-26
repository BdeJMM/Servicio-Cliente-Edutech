package com.edutech.servicioalcliente.service;

import com.edutech.servicioalcliente.model.edutechmodel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EdutechImplem implements EdutechService {

    @Autowired
    private EdutechRepository edutechrepository;

    @Override
    public List<edutechmodel> obtenerTodos() {
        return edutechrepository.findAll();
    }

    @Override
    public Optional<edutechmodel> obtenerPorId(Long id) {
        return edutechrepository.findById(id);
    }

    @Override
    public edutechmodel guardar(edutechmodel ticket) {
        // Si es un ticket nuevo, establecer fecha de creación y estado por defecto
        if (ticket.getId() == 0) { // ← Cambiado aquí
            ticket.setFechacreacion(LocalDateTime.now());
            if (ticket.getEstado() == null || ticket.getEstado().isEmpty()) {
            ticket.setEstado("ABIERTA");
        }
    }
    return edutechrepository.save(ticket);
    }


    @Override
    public void eliminar(Long id) {
        edutechrepository.deleteById(id);
    }

    @Override
    public List<edutechmodel> obtenerTicketsPorCliente(Long clienteId) {
        return edutechrepository.findByClienteid(clienteId);
    }

    @Override
    public List<edutechmodel> obtenerTicketsPorEstado(String estado) {
        return edutechrepository.findByEstado(estado);
    }
}