package com.edutech.servicioalcliente;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edutech.servicioalcliente.model.EdutechModel;
import com.edutech.servicioalcliente.repository.EdutechRepository;
import com.edutech.servicioalcliente.service.EdutecImplem;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private EdutechRepository repository;

    @InjectMocks
    private EdutecImplem service;

    private EdutechModel ticket;

    @BeforeEach
    void setUp() {
        ticket = EdutechModel.builder()
                .id(1L)
                .titulo("Problema con login")
                .descripcion("No puedo iniciar sesión")
                .estado("ABIERTA")
                .clienteid(100L)
                .fechacreacion(LocalDateTime.now())
                .build();
    }

    @Test
    void testObtenerTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(ticket));
        List<EdutechModel> result = service.obtenerTodos();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticket.getId(), result.get(0).getId());
    }

    @Test
    void testObtenerPorId() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(ticket));
        var result = service.obtenerPorId(1L);
        assertTrue(result.isPresent());
        assertEquals(ticket.getId(), result.get().getId());
    }

    @Test
    void testGuardar() {
        when(repository.save(any(EdutechModel.class))).thenReturn(ticket);
        EdutechModel saved = service.guardar(ticket);
        assertNotNull(saved);
        assertEquals(ticket.getTitulo(), saved.getTitulo());
    }

    @Test
    void testEliminar() {
        doNothing().when(repository).deleteById(1L);
        service.eliminar(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testObtenerTicketsPorCliente() {
        when(repository.findByClienteid(100L)).thenReturn(Arrays.asList(ticket));
        List<EdutechModel> result = service.obtenerTicketsPorCliente(100L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getClienteid());
    }

    @Test
    void testObtenerTicketsPorEstado() {
        when(repository.findByEstado("ABIERTA")).thenReturn(Arrays.asList(ticket));
        List<EdutechModel> result = service.obtenerTicketsPorEstado("ABIERTA");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABIERTA", result.get(0).getEstado());
    }
}