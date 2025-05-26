package com.edutech.servicioalcliente.controller;

import com.edutech.servicioalcliente.model.edutechmodel;
import com.edutech.servicioalcliente.service.edutechservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/edutech")
public class edutechcontroller {

    @Autowired
    private edutechservice edutechservice;

    @GetMapping("/ping")
    public String probar() {
        return "Microservicio Servicio al Cliente Activo";
    }

    /**
     * Obtener todos los tickets existentes.
     * Método HTTP: GET
     * Endpoint: /api/v1/tickets
     * @return Lista de todos los tickets
     */
    @GetMapping
    public List<edutechmodel> obtenerTodosLosTickets(){
        return edutechservice.obtenerTodos();
    }

    /**
     * Obtener un ticket por su ID.
     * Método HTTP: GET
     * Endpoint: /api/v1/tickets/{id}
     * @param id ID del ticket
     * @return El ticket encontrado o error 404 si no existe
     */
    @GetMapping("/{id}")
    public edutechmodel obtenerTicketPorId(@PathVariable Long id) {
        return edutechservice.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con ID: " + id));
    }

    /**
     * Crear un nuevo ticket de soporte.
     * Método HTTP: POST
     * Endpoint: /api/v1/tickets
     * @param ticket Ticket a guardar
     * @return Ticket creado con código 201
     */
    @PostMapping
    public ResponseEntity<edutechmodel> crearTicket(@RequestBody edutechmodel ticket){
        edutechmodel nuevo = edutechservice.guardar(ticket);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    /**
     * Actualizar un ticket existente por ID.
     * Método HTTP: PUT
     * Endpoint: /api/v1/tickets/{id}
     * @param id ID del ticket a actualizar
     * @param ticket Datos nuevos del ticket
     * @return Ticket actualizado o error 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<edutechmodel> actualizarTicket(@PathVariable Long id, @RequestBody edutechmodel ticket) {
        Optional<edutechmodel> existente = edutechservice.obtenerPorId(id);
        if (existente.isPresent()) {
            edutechmodel actual = existente.get();
            actual.setTitulo(ticket.getTitulo());
            actual.setDescripcion(ticket.getDescripcion());
            actual.setEstado(ticket.getEstado());
            actual.setClienteid(ticket.getClienteid());
            return ResponseEntity.ok(edutechservice.guardar(actual));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar un ticket por ID.
     * Método HTTP: DELETE
     * Endpoint: /api/v1/tickets/{id}
     * @param id ID del ticket a eliminar
     * @return Código 204 si se eliminó o 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        Optional<edutechmodel> existente = edutechservice.obtenerPorId(id);
        if (existente.isPresent()) {
            edutechservice.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // ========== MÉTODOS ÚTILES PARA SERVICIO AL CLIENTE ==========

    /**
     * Obtener tickets por cliente específico.
     * Método HTTP: GET
     * Endpoint: /api/v1/tickets/cliente/{clienteId}
     * @param clienteId ID del cliente
     * @return Lista de tickets del cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<edutechmodel>> obtenerTicketsPorCliente(@PathVariable Long clienteId) {
        List<edutechmodel> tickets = edutechservice.obtenerTicketsPorCliente(clienteId);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    /**
     * Cambiar estado de un ticket (cerrar, abrir, en proceso).
     * Método HTTP: PATCH
     * Endpoint: /api/v1/tickets/{id}/estado
     * @param id ID del ticket
     * @param nuevoEstado Nuevo estado (ABIERTA, CERRADA, EN_PROCESO)
     * @return Ticket con estado actualizado
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<edutechmodel> cambiarEstadoTicket(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado) {
        Optional<edutechmodel> existente = edutechservice.obtenerPorId(id);
        if (existente.isPresent()) {
            edutechmodel ticket = existente.get();
            ticket.setEstado(nuevoEstado.toUpperCase());
            edutechmodel actualizado = edutechservice.guardar(ticket);
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener tickets por estado específico.
     * Método HTTP: GET
     * Endpoint: /api/v1/tickets/estado/{estado}
     * @param estado Estado a filtrar (ABIERTA, CERRADA, EN_PROCESO)
     * @return Lista de tickets con el estado especificado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<edutechmodel>> obtenerTicketsPorEstado(@PathVariable String estado) {
        List<edutechmodel> tickets = edutechservice.obtenerTicketsPorEstado(estado.toUpperCase());
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }
    
}