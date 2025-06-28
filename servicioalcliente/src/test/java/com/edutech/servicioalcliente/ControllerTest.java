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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ControllerTest {

    @Mock
    private EdutechService service;

    @InjectMocks
    private EdutechController controller;

    @Test
    void testProbarPing() {
        assertEquals("Microservicio Servicio al Cliente Activo", controller.probar());
    }

    @Test
    void testObtenerTodosLosTickets() {
        EdutechModel ticket1 = new EdutechModel();
        ticket1.setId(1L);
        EdutechModel ticket2 = new EdutechModel();
        ticket2.setId(2L);
        when(service.obtenerTodos()).thenReturn(Arrays.asList(ticket1, ticket2));
        CollectionModel<EntityModel<EdutechModel>> result = controller.obtenerTodosLosTickets();
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testObtenerTicketPorId() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
        EntityModel<EdutechModel> result = controller.obtenerTicketPorId(1L);
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1L, result.getContent().getId());
    }

    @Test
    void testCrearTicket() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        when(service.guardar(any(EdutechModel.class))).thenReturn(ticket);
        ResponseEntity<EntityModel<EdutechModel>> response = controller.crearTicket(ticket);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1L, response.getBody().getContent().getId());
    }

    @Test
    void testActualizarTicket() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        ticket.setTitulo("Old");
        EdutechModel updated = new EdutechModel();
        updated.setId(1L);
        updated.setTitulo("New");
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
        when(service.guardar(any(EdutechModel.class))).thenReturn(updated);
        ResponseEntity<EntityModel<EdutechModel>> response = controller.actualizarTicket(1L, updated);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("New", response.getBody().getContent().getTitulo());
    }

    @Test
    void testEliminarTicket() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
        doNothing().when(service).eliminar(1L);
        ResponseEntity<Void> response = controller.eliminarTicket(1L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void testObtenerTicketsPorCliente() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        when(service.obtenerTicketsPorCliente(1L)).thenReturn(Arrays.asList(ticket));
        ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorCliente(1L);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void testCambiarEstadoTicket() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        ticket.setEstado("ABIERTA");
        EdutechModel actualizado = new EdutechModel();
        actualizado.setId(1L);
        actualizado.setEstado("CERRADA");
        when(service.obtenerPorId(1L)).thenReturn(Optional.of(ticket));
        when(service.guardar(any(EdutechModel.class))).thenReturn(actualizado);
        ResponseEntity<EntityModel<EdutechModel>> response = controller.cambiarEstadoTicket(1L, "CERRADA");
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("CERRADA", response.getBody().getContent().getEstado());
    }

    @Test
    void testObtenerTicketsPorEstado() {
        EdutechModel ticket = new EdutechModel();
        ticket.setId(1L);
        ticket.setEstado("ABIERTA");
        when(service.obtenerTicketsPorEstado("ABIERTA")).thenReturn(Arrays.asList(ticket));
        ResponseEntity<CollectionModel<EntityModel<EdutechModel>>> response = controller.obtenerTicketsPorEstado("ABIERTA");
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }
}