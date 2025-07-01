package com.edutech.servicioalcliente.controller;

import com.edutech.servicioalcliente.model.EdutechModel;
import com.edutech.servicioalcliente.service.EdutechService;
import com.edutech.servicioalcliente.assembler.EdutechModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/edutech")
public class EdutechController{

    @Autowired
    private EdutechService edutechservice;

    @Autowired
    private EdutechModelAssembler assembler;

    @GetMapping("/ping")
    public String probar() {
        return "Microservicio Servicio al Cliente Activo";
    }

    /**
     * Obtener todos los tickets existentes.
     * Método HTTP: GET
     * Endpoint: /
     * @return Lista de todos los tickets
     */
    @GetMapping
    public CollectionModel<EntityModel<EdutechModel>> obtenerTodosLosTickets() {
        try {
            List<EdutechModel> tickets = edutechservice.obtenerTodos();
            List<EntityModel<EdutechModel>> ticketResources = tickets.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());
            return CollectionModel.of(ticketResources,
                    linkTo(methodOn(EdutechController.class).obtenerTodosLosTickets()).withSelfRel()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los tickets", e);
        }
    }

    /**
     * Obtener un ticket por su ID.
     * Método HTTP: GET
     * Endpoint: /api/v1/tickets/{id}
     * @param id ID del ticket
     * @return El ticket encontrado o error 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EdutechModel>> obtenerTicketPorId(@PathVariable Long id) {
        try {
            EdutechModel ticket = edutechservice.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Ticket no encontrado con ID: " + id));
            return ResponseEntity.ok(assembler.toModel(ticket));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crear un nuevo ticket de soporte.
     * Método HTTP: POST
     * Endpoint: /api/v1/tickets
     * @param ticket Ticket a guardar
     * @return Ticket creado con código 201
     */ 
    @PostMapping
    public ResponseEntity<EntityModel<EdutechModel>> crearTicket(@RequestBody EdutechModel ticket) {
        try {
            EdutechModel nuevo = edutechservice.guardar(ticket);
            return new ResponseEntity<>(assembler.toModel(nuevo), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    public ResponseEntity<EntityModel<EdutechModel>> actualizarTicket(@PathVariable Long id, @RequestBody EdutechModel ticket) {
        try {
            Optional<EdutechModel> existente = edutechservice.obtenerPorId(id);
            if (existente.isPresent()) {
                EdutechModel actual = existente.get();
                actual.setTitulo(ticket.getTitulo());
                actual.setDescripcion(ticket.getDescripcion());
                actual.setEstado(ticket.getEstado());
                actual.setClienteid(ticket.getClienteid());
                EdutechModel actualizado = edutechservice.guardar(actual);
                return ResponseEntity.ok(assembler.toModel(actualizado));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
        try {
            Optional<EdutechModel> existente = edutechservice.obtenerPorId(id);
            if (existente.isPresent()) {
                edutechservice.eliminar(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtener tickets por cliente específico.
     * Método HTTP: GET
     * Endpoint: /api/v1/tickets/cliente/{clienteId}
     * @param clienteId ID del cliente
     * @return Lista de tickets del cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> obtenerTicketsPorCliente(@PathVariable Long clienteId) {
        try {
            List<EdutechModel> tickets = edutechservice.obtenerTicketsPorCliente(clienteId);
            List<EntityModel<EdutechModel>> ticketResources = tickets.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());
            if (ticketResources.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(CollectionModel.of(ticketResources,
                    linkTo(methodOn(EdutechController.class).obtenerTicketsPorCliente(clienteId)).withSelfRel()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    public ResponseEntity<EntityModel<EdutechModel>> cambiarEstadoTicket(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado) {
        try {
            Optional<EdutechModel> existente = edutechservice.obtenerPorId(id);
            if (existente.isPresent()) {
                EdutechModel ticket = existente.get();
                ticket.setEstado(nuevoEstado.toUpperCase());
                EdutechModel actualizado = edutechservice.guardar(ticket);
                return ResponseEntity.ok(assembler.toModel(actualizado));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    public ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> obtenerTicketsPorEstado(@PathVariable String estado) {
        try {
            List<EdutechModel> tickets = edutechservice.obtenerTicketsPorEstado(estado.toUpperCase());
            List<EntityModel<EdutechModel>> ticketResources = tickets.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());
            if (ticketResources.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(CollectionModel.of(ticketResources,
                    linkTo(methodOn(EdutechController.class).obtenerTicketsPorEstado(estado)).withSelfRel()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // HATEOAS helper removed; now using EdutechModelAssembler
}