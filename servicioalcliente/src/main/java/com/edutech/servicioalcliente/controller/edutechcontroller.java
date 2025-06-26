package com.edutech.servicioalcliente.controller;

import com.edutech.servicioalcliente.model.edutechmodel;
import com.edutech.servicioalcliente.service.edutechservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
     * Obtener todos los tickets existentes con enlaces HATEOAS.
     */
    @GetMapping
    public CollectionModel<EntityModel<edutechmodel>> obtenerTodosLosTickets(){
        List<EntityModel<edutechmodel>> tickets = edutechservice.obtenerTodos().stream()
            .map(this::toEntityModel)
            .collect(Collectors.toList());
        
        return CollectionModel.of(tickets)
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTodosLosTickets()).withSelfRel())
            .add(linkTo(methodOn(edutechcontroller.class).crearTicket(null)).withRel("create"));
    }
    
    /**
     * Obtener un ticket por su ID con enlaces HATEOAS.
     */
    @GetMapping("/{id}")
    public EntityModel<edutechmodel> obtenerTicketPorId(@PathVariable Long id) {
        edutechmodel ticket = edutechservice.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado con ID: " + id));
        
        return toEntityModel(ticket);
    }

    /**
     * Crear un nuevo ticket con enlaces HATEOAS.
     */ 
    @PostMapping
    public ResponseEntity<EntityModel<edutechmodel>> crearTicket(@RequestBody edutechmodel ticket){
        edutechmodel nuevo = edutechservice.guardar(ticket);
        EntityModel<edutechmodel> entityModel = toEntityModel(nuevo);
        
        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    /**
     * Actualizar un ticket existente con enlaces HATEOAS.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<edutechmodel>> actualizarTicket(@PathVariable Long id, @RequestBody edutechmodel ticket) {
        Optional<edutechmodel> existente = edutechservice.obtenerPorId(id);
        if (existente.isPresent()) {
            edutechmodel actual = existente.get();
            actual.setTitulo(ticket.getTitulo());
            actual.setDescripcion(ticket.getDescripcion());
            actual.setEstado(ticket.getEstado());
            actual.setClienteid(ticket.getClienteid());
            
            edutechmodel actualizado = edutechservice.guardar(actual);
            return ResponseEntity.ok(toEntityModel(actualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar un ticket por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        Optional<edutechmodel> existente = edutechservice.obtenerPorId(id);
        if (existente.isPresent()) {
            edutechservice.eliminar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener tickets por cliente con enlaces HATEOAS.
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CollectionModel<EntityModel<edutechmodel>>> obtenerTicketsPorCliente(@PathVariable Long clienteId) {
        List<edutechmodel> tickets = edutechservice.obtenerTicketsPorCliente(clienteId);
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        List<EntityModel<edutechmodel>> ticketModels = tickets.stream()
            .map(this::toEntityModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<edutechmodel>> collectionModel = CollectionModel.of(ticketModels)
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTicketsPorCliente(clienteId)).withSelfRel())
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTodosLosTickets()).withRel("all-tickets"));
        
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Cambiar estado de un ticket con enlaces HATEOAS.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntityModel<edutechmodel>> cambiarEstadoTicket(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado) {
        Optional<edutechmodel> existente = edutechservice.obtenerPorId(id);
        if (existente.isPresent()) {
            edutechmodel ticket = existente.get();
            ticket.setEstado(nuevoEstado.toUpperCase());
            edutechmodel actualizado = edutechservice.guardar(ticket);
            return ResponseEntity.ok(toEntityModel(actualizado));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener tickets por estado con enlaces HATEOAS.
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<EntityModel<edutechmodel>>> obtenerTicketsPorEstado(@PathVariable String estado) {
        List<edutechmodel> tickets = edutechservice.obtenerTicketsPorEstado(estado.toUpperCase());
        if (tickets.isEmpty()) {    
            return ResponseEntity.noContent().build();
        }
        
        List<EntityModel<edutechmodel>> ticketModels = tickets.stream()
            .map(this::toEntityModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<edutechmodel>> collectionModel = CollectionModel.of(ticketModels)
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTicketsPorEstado(estado)).withSelfRel())
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTodosLosTickets()).withRel("all-tickets"));
        
        return ResponseEntity.ok(collectionModel);
    }
    
    /**
     * Método auxiliar para crear EntityModel con enlaces HATEOAS.
     */
    private EntityModel<edutechmodel> toEntityModel(edutechmodel ticket) {
        return EntityModel.of(ticket)
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTicketPorId(ticket.getId())).withSelfRel())
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTodosLosTickets()).withRel("all-tickets"))
            .add(linkTo(methodOn(edutechcontroller.class).actualizarTicket(ticket.getId(), null)).withRel("update"))
            .add(linkTo(methodOn(edutechcontroller.class).eliminarTicket(ticket.getId())).withRel("delete"))
            .add(linkTo(methodOn(edutechcontroller.class).cambiarEstadoTicket(ticket.getId(), null)).withRel("change-status"))
            .add(linkTo(methodOn(edutechcontroller.class).obtenerTicketsPorCliente(ticket.getClienteid())).withRel("client-tickets"));
    }
}