package com.edutech.servicioalcliente.service;

import com.edutech.servicioalcliente.model.EdutechModel;
import com.edutech.servicioalcliente.repository.EdutechRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EdutecImplem implements EdutechService {

    @Autowired
    private EdutechRepository edutechrepository;

    @Override
    public List<EdutechModel> obtenerTodos() {
        return edutechrepository.findAll();
    }

    @Override
    public Optional<EdutechModel> obtenerPorId(Long id) {
        return edutechrepository.findById(id);
    }

    @Override
    public EdutechModel guardar(EdutechModel ticket) {
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
    public List<EdutechModel> obtenerTicketsPorCliente(Long clienteId) {
        return edutechrepository.findByClienteid(clienteId);
    }

    @Override
    public List<EdutechModel> obtenerTicketsPorEstado(String estado) {
        return edutechrepository.findByEstado(estado);
    }
}