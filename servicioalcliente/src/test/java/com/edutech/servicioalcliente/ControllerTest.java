package com.edutech.servicioalcliente;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.edutech.servicioalcliente.controller.EdutechController;
import com.edutech.servicioalcliente.service.EdutechService;
import com.edutech.servicioalcliente.model.EdutechModel;
import com.edutech.servicioalcliente.assembler.EdutechModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @Mock
    private EdutechService service;

    @Mock
    private EdutechModelAssembler assembler;

    @InjectMocks
    private EdutechController controller;

    @Test
    void testProbarPing() {
        assertEquals("Microservicio Servicio al Cliente Activo", controller.probar());
    }

    @Test
    void testObtenerTodosLosTickets() {
        try {
            EdutechModel ticket1 = new EdutechModel();
            ticket1.setId(1L);
            EdutechModel ticket2 = new EdutechModel();
            ticket2.setId(2L);
            when(service.obtenerTodos()).thenReturn(Arrays.asList(ticket1, ticket2));
            when(assembler.toModel(ticket1)).thenReturn(EntityModel.of(ticket1));
            when(assembler.toModel(ticket2)).thenReturn(EntityModel.of(ticket2));
            CollectionModel<EntityModel<EdutechModel>> result = controller.obtenerTodosLosTickets();
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testObtenerTodosLosTicketsException() {
        when(service.obtenerTodos()).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> controller.obtenerTodosLosTickets());
    }

    @Test
    void testObtenerTicketPorId() {
        try {
            EdutechModel ticket = new EdutechModel();
            ticket.setId(1L);
            when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
            when(assembler.toModel(ticket)).thenReturn(EntityModel.of(ticket));
            ResponseEntity<EntityModel<EdutechModel>> result = controller.obtenerTicketPorId(1L);
            assertEquals(200, result.getStatusCode().value());
            assertNotNull(result.getBody());
            assertNotNull(result.getBody().getContent());
            assertEquals(1L, result.getBody().getContent().getId());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testObtenerTicketPorIdNotFound() {
        when(service.obtenerPorId(1L)).thenReturn(Optional.empty());
        ResponseEntity<EntityModel<EdutechModel>> result = controller.obtenerTicketPorId(1L);
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void testCrearTicket() {
        try {
            EdutechModel ticket = new EdutechModel();
            ticket.setId(1L);
            when(service.guardar(any(EdutechModel.class))).thenReturn(ticket);
            when(assembler.toModel(ticket)).thenReturn(EntityModel.of(ticket));
            ResponseEntity<EntityModel<EdutechModel>> response = controller.crearTicket(ticket);
            assertEquals(201, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getContent());
            assertEquals(1L, response.getBody().getContent().getId());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testCrearTicketException() {
        EdutechModel ticket = new EdutechModel();
        when(service.guardar(any(EdutechModel.class))).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<EntityModel<EdutechModel>> response = controller.crearTicket(ticket);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testActualizarTicket() {
        try {
            EdutechModel ticket = new EdutechModel();
            ticket.setId(1L);
            ticket.setTitulo("Old");
            EdutechModel updated = new EdutechModel();
            updated.setId(1L);
            updated.setTitulo("New");
            when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
            when(service.guardar(any(EdutechModel.class))).thenReturn(updated);
            when(assembler.toModel(updated)).thenReturn(EntityModel.of(updated));
            ResponseEntity<EntityModel<EdutechModel>> response = controller.actualizarTicket(1L, updated);
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals("New", response.getBody().getContent().getTitulo());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testActualizarTicketNotFound() {
        EdutechModel updated = new EdutechModel();
        updated.setId(1L);
        when(service.obtenerPorId(1L)).thenReturn(Optional.empty());
        ResponseEntity<EntityModel<EdutechModel>> response = controller.actualizarTicket(1L, updated);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testActualizarTicketException() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
        when(service.guardar(any(EdutechModel.class))).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<EntityModel<EdutechModel>> response = controller.actualizarTicket(1L, ticket);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testEliminarTicket() {
        try {
            EdutechModel ticket = new EdutechModel();
            ticket.setId(1L);
            when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
            doNothing().when(service).eliminar(1L);
            ResponseEntity<Void> response = controller.eliminarTicket(1L);
            assertEquals(204, response.getStatusCode().value());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testEliminarTicketNotFound() {
        when(service.obtenerPorId(1L)).thenReturn(Optional.empty());
        ResponseEntity<Void> response = controller.eliminarTicket(1L);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testEliminarTicketException() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
        doThrow(new RuntimeException("DB error")).when(service).eliminar(1L);
        ResponseEntity<Void> response = controller.eliminarTicket(1L);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testObtenerTicketsPorCliente() {
        try {
            EdutechModel ticket = new EdutechModel();
            ticket.setId(1L);
            when(service.obtenerTicketsPorCliente(1L)).thenReturn(Arrays.asList(ticket));
            when(assembler.toModel(ticket)).thenReturn(EntityModel.of(ticket));
            ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorCliente(1L);
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getContent().size());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testObtenerTicketsPorClienteNoContent() {
        when(service.obtenerTicketsPorCliente(1L)).thenReturn(Collections.emptyList());
        ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorCliente(1L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void testObtenerTicketsPorClienteException() {
        when(service.obtenerTicketsPorCliente(1L)).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorCliente(1L);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testCambiarEstadoTicket() {
        try {
            EdutechModel ticket = new EdutechModel();
            ticket.setId(1L);
            ticket.setEstado("ABIERTA");
            EdutechModel actualizado = new EdutechModel();
            actualizado.setId(1L);
            actualizado.setEstado("CERRADA");
            when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
            when(service.guardar(any(EdutechModel.class))).thenReturn(actualizado);
            when(assembler.toModel(actualizado)).thenReturn(EntityModel.of(actualizado));
            ResponseEntity<EntityModel<EdutechModel>> response = controller.cambiarEstadoTicket(1L, "CERRADA");
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals("CERRADA", response.getBody().getContent().getEstado());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testCambiarEstadoTicketNotFound() {
        when(service.obtenerPorId(1L)).thenReturn(Optional.empty());
        ResponseEntity<EntityModel<EdutechModel>> response = controller.cambiarEstadoTicket(1L, "CERRADA");
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testCambiarEstadoTicketException() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
        when(service.guardar(any(EdutechModel.class))).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<EntityModel<EdutechModel>> response = controller.cambiarEstadoTicket(1L, "CERRADA");
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testObtenerTicketsPorEstado() {
        try {
            EdutechModel ticket = new EdutechModel();
            ticket.setId(1L);
            ticket.setEstado("ABIERTA");
            when(service.obtenerTicketsPorEstado("ABIERTA")).thenReturn(Arrays.asList(ticket));
            when(assembler.toModel(ticket)).thenReturn(EntityModel.of(ticket));
            ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorEstado("ABIERTA");
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getContent().size());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testObtenerTicketsPorEstadoNoContent() {
        when(service.obtenerTicketsPorEstado("ABIERTA")).thenReturn(Collections.emptyList());
        ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorEstado("ABIERTA");
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void testObtenerTicketsPorEstadoException() {
        when(service.obtenerTicketsPorEstado("ABIERTA")).thenThrow(new RuntimeException("DB error"));
        ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorEstado("ABIERTA");
        assertEquals(500, response.getStatusCode().value());
    }
}