package com.edutech.servicioalcliente.service;  

import com.edutech.servicioalcliente.model.edutechmodel;
import com.edutech.servicioalcliente.repository.edutechrepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio EdutechService.
 * 
 * Esta clase contiene todas las pruebas unitarias para verificar el comportamiento
 * correcto de la lógica de negocio del servicio de tickets de soporte.
 * 
 * @author Sistema EduTech
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para EdutechService")
class EdutechServiceTest {

    @Mock
    private edutechrepository edutechRepository;

    @InjectMocks
    private edutechimplem edutechService;

    private edutechmodel ticketEjemplo;
    private List<edutechmodel> listaTickets;

    /**
     * Configuración inicial para cada prueba.
     * Crea objetos de prueba que serán utilizados en los diferentes tests.
     */
    @BeforeEach
    void setUp() {
        ticketEjemplo = edutechmodel.builder()
                .id(1L)
                .titulo("Problema con la plataforma")
                .descripcion("No puedo acceder a mi curso")
                .estado("ABIERTA")
                .clienteid(100L)
                .fechacreacion(LocalDateTime.now())
                .build();

        edutechmodel ticket2 = edutechmodel.builder()
                .id(2L)
                .titulo("Error en el sistema")
                .descripcion("La página no carga correctamente")
                .estado("EN_PROCESO")
                .clienteid(101L)
                .fechacreacion(LocalDateTime.now())
                .build();

        listaTickets = Arrays.asList(ticketEjemplo, ticket2);
    }

    /**
     * Prueba la obtención de todos los tickets.
     * Verifica que el servicio retorna correctamente todos los tickets del repositorio.
     */
    @Test
    @DisplayName("Debe obtener todos los tickets correctamente")
    void testObtenerTodos() {
        // Given - Arrange
        when(edutechRepository.findAll()).thenReturn(listaTickets);

        // When - Act
        List<edutechmodel> resultado = edutechService.obtenerTodos();

        // Then - Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Problema con la plataforma", resultado.get(0).getTitulo());
        assertEquals("Error en el sistema", resultado.get(1).getTitulo());
        
        verify(edutechRepository, times(1)).findAll();
    }

    /**
     * Prueba la obtención de todos los tickets cuando la lista está vacía.
     * Verifica que el servicio maneja correctamente una lista vacía.
     */
    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay tickets")
    void testObtenerTodosListaVacia() {
        // Given
        when(edutechRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<edutechmodel> resultado = edutechService.obtenerTodos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        
        verify(edutechRepository, times(1)).findAll();
    }

    /**
     * Prueba la obtención de un ticket por ID cuando existe.
     * Verifica que el servicio retorna el ticket correcto.
     */
    @Test
    @DisplayName("Debe obtener un ticket por ID cuando existe")
    void testObtenerPorIdExistente() {
        // Given
        when(edutechRepository.findById(1L)).thenReturn(Optional.of(ticketEjemplo));

        // When
        Optional<edutechmodel> resultado = edutechService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Problema con la plataforma", resultado.get().getTitulo());
        
        verify(edutechRepository, times(1)).findById(1L);
    }

    /**
     * Prueba la obtención de un ticket por ID cuando no existe.
     * Verifica que el servicio retorna Optional vacío.
     */
    @Test
    @DisplayName("Debe retornar Optional vacío cuando el ticket no existe")
    void testObtenerPorIdNoExistente() {
        // Given
        when(edutechRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<edutechmodel> resultado = edutechService.obtenerPorId(999L);

        // Then
        assertFalse(resultado.isPresent());
        
        verify(edutechRepository, times(1)).findById(999L);
    }

    /**
     * Prueba el guardado de un ticket nuevo.
     * Verifica que se establecen correctamente la fecha de creación y el estado por defecto.
     */
    @Test
    @DisplayName("Debe guardar un ticket nuevo con fecha y estado por defecto")
    void testGuardarTicketNuevo() {
        // Given
        edutechmodel ticketNuevo = edutechmodel.builder()
                .id(0L) // ID 0 indica que es nuevo
                .titulo("Nuevo ticket")
                .descripcion("Descripción del nuevo ticket")
                .clienteid(102L)
                .build();

        edutechmodel ticketGuardado = edutechmodel.builder()
                .id(3L)
                .titulo("Nuevo ticket")
                .descripcion("Descripción del nuevo ticket")
                .estado("ABIERTA")
                .clienteid(102L)
                .fechacreacion(LocalDateTime.now())
                .build();

        when(edutechRepository.save(any(edutechmodel.class))).thenReturn(ticketGuardado);

        // When
        edutechmodel resultado = edutechService.guardar(ticketNuevo);

        // Then
        assertNotNull(resultado);
        assertEquals("Nuevo ticket", resultado.getTitulo());
        assertEquals("ABIERTA", resultado.getEstado());
        assertNotNull(resultado.getFechacreacion());
        
        verify(edutechRepository, times(1)).save(any(edutechmodel.class));
    }

    /**
     * Prueba el guardado de un ticket existente (actualización).
     * Verifica que se actualiza sin modificar la fecha de creación original.
     */
    @Test
    @DisplayName("Debe actualizar un ticket existente sin cambiar fecha de creación")
    void testGuardarTicketExistente() {
        // Given
        edutechmodel ticketExistente = edutechmodel.builder()
                .id(1L) // ID > 0 indica que ya existe
                .titulo("Ticket actualizado")
                .descripcion("Descripción actualizada")
                .estado("EN_PROCESO")
                .clienteid(100L)
                .fechacreacion(LocalDateTime.now().minusDays(1))
                .build();

        when(edutechRepository.save(ticketExistente)).thenReturn(ticketExistente);

        // When
        edutechmodel resultado = edutechService.guardar(ticketExistente);

        // Then
        assertNotNull(resultado);
        assertEquals("Ticket actualizado", resultado.getTitulo());
        assertEquals("EN_PROCESO", resultado.getEstado());
        
        verify(edutechRepository, times(1)).save(ticketExistente);
    }

    /**
     * Prueba el guardado de un ticket nuevo sin estado definido.
     * Verifica que se establece el estado "ABIERTA" por defecto.
     */
    @Test
    @DisplayName("Debe establecer estado ABIERTA por defecto en ticket nuevo")
    void testGuardarTicketNuevoSinEstado() {
        // Given
        edutechmodel ticketSinEstado = edutechmodel.builder()
                .id(0L)
                .titulo("Ticket sin estado")
                .descripcion("Descripción")
                .clienteid(103L)
                .estado(null) // Sin estado definido
                .build();

        when(edutechRepository.save(any(edutechmodel.class))).thenAnswer(invocation -> {
            edutechmodel ticket = invocation.getArgument(0);
            ticket.setId(4L);
            return ticket;
        });

        // When
        edutechmodel resultado = edutechService.guardar(ticketSinEstado);

        // Then
        assertNotNull(resultado);
        assertEquals("ABIERTA", ticketSinEstado.getEstado()); // Verificamos que se estableció
        assertNotNull(ticketSinEstado.getFechacreacion()); // Verificamos que se estableció la fecha
        
        verify(edutechRepository, times(1)).save(any(edutechmodel.class));
    }

    /**
     * Prueba la eliminación de un ticket por ID.
     * Verifica que se llama correctamente al método del repositorio.
     */
    @Test
    @DisplayName("Debe eliminar un ticket por ID")
    void testEliminar() {
        // Given
        Long idTicket = 1L;
        doNothing().when(edutechRepository).deleteById(idTicket);

        // When
        edutechService.eliminar(idTicket);

        // Then
        verify(edutechRepository, times(1)).deleteById(idTicket);
    }

    /**
     * Prueba la obtención de tickets por cliente.
     * Verifica que retorna correctamente los tickets del cliente especificado.
     */
    @Test
    @DisplayName("Debe obtener tickets por cliente correctamente")
    void testObtenerTicketsPorCliente() {
        // Given
        Long clienteId = 100L;
        List<edutechmodel> ticketsCliente = Arrays.asList(ticketEjemplo);
        when(edutechRepository.findByClienteid(clienteId)).thenReturn(ticketsCliente);

        // When
        List<edutechmodel> resultado = edutechService.obtenerTicketsPorCliente(clienteId);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(clienteId, resultado.get(0).getClienteid());
        
        verify(edutechRepository, times(1)).findByClienteid(clienteId);
    }

    /**
     * Prueba la obtención de tickets por cliente cuando no hay tickets.
     * Verifica que retorna una lista vacía.
     */
    @Test
    @DisplayName("Debe retornar lista vacía cuando cliente no tiene tickets")
    void testObtenerTicketsPorClienteSinTickets() {
        // Given
        Long clienteId = 999L;
        when(edutechRepository.findByClienteid(clienteId)).thenReturn(Collections.emptyList());

        // When
        List<edutechmodel> resultado = edutechService.obtenerTicketsPorCliente(clienteId);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        
        verify(edutechRepository, times(1)).findByClienteid(clienteId);
    }

    /**
     * Prueba la obtención de tickets por estado.
     * Verifica que retorna correctamente los tickets con el estado especificado.
     */
    @Test
    @DisplayName("Debe obtener tickets por estado correctamente")
    void testObtenerTicketsPorEstado() {
        // Given
        String estado = "ABIERTA";
        List<edutechmodel> ticketsAbiertos = Arrays.asList(ticketEjemplo);
        when(edutechRepository.findByEstado(estado)).thenReturn(ticketsAbiertos);

        // When
        List<edutechmodel> resultado = edutechService.obtenerTicketsPorEstado(estado);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(estado, resultado.get(0).getEstado());
        
        verify(edutechRepository, times(1)).findByEstado(estado);
    }

    /**
     * Prueba la obtención de tickets por estado cuando no hay tickets.
     * Verifica que retorna una lista vacía.
     */
    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay tickets con el estado especificado")
    void testObtenerTicketsPorEstadoSinTickets() {
        // Given
        String estado = "CERRADA";
        when(edutechRepository.findByEstado(estado)).thenReturn(Collections.emptyList());

        // When
        List<edutechmodel> resultado = edutechService.obtenerTicketsPorEstado(estado);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        
        verify(edutechRepository, times(1)).findByEstado(estado);
    }

    /**
     * Prueba el guardado con valores nulos para verificar robustez.
     * Verifica que el servicio maneja correctamente casos edge.
     */
    @Test
    @DisplayName("Debe manejar correctamente ticket con estado vacío")
    void testGuardarTicketConEstadoVacio() {
        // Given
        edutechmodel ticketEstadoVacio = edutechmodel.builder()
                .id(0L)
                .titulo("Ticket con estado vacío")
                .descripcion("Descripción")
                .clienteid(104L)
                .estado("") // Estado vacío
                .build();

        when(edutechRepository.save(any(edutechmodel.class))).thenAnswer(invocation -> {
            edutechmodel ticket = invocation.getArgument(0);
            ticket.setId(5L);
            return ticket;
        });

        // When
        edutechmodel resultado = edutechService.guardar(ticketEstadoVacio);

        // Then
        assertNotNull(resultado);
        assertEquals("ABIERTA", ticketEstadoVacio.getEstado()); // Debe establecer estado por defecto
        assertNotNull(ticketEstadoVacio.getFechacreacion()); // Debe establecer fecha
        
        verify(edutechRepository, times(1)).save(any(edutechmodel.class));
    }

    /**
     * Prueba la búsqueda de tickets por múltiples clientes.
     * Verifica el comportamiento con diferentes IDs de cliente.
     */
    @Test
    @DisplayName("Debe buscar tickets para diferentes clientes")
    void testObtenerTicketsMultiplesClientes() {
        // Given
        Long cliente1 = 100L;
        Long cliente2 = 101L;
        
        List<edutechmodel> ticketsCliente1 = Arrays.asList(ticketEjemplo);
        List<edutechmodel> ticketsCliente2 = Arrays.asList(listaTickets.get(1));
        
        when(edutechRepository.findByClienteid(cliente1)).thenReturn(ticketsCliente1);
        when(edutechRepository.findByClienteid(cliente2)).thenReturn(ticketsCliente2);

        // When
        List<edutechmodel> resultadoCliente1 = edutechService.obtenerTicketsPorCliente(cliente1);
        List<edutechmodel> resultadoCliente2 = edutechService.obtenerTicketsPorCliente(cliente2);

        // Then
        assertEquals(1, resultadoCliente1.size());
        assertEquals(cliente1, resultadoCliente1.get(0).getClienteid());
        
        assertEquals(1, resultadoCliente2.size());
        assertEquals(cliente2, resultadoCliente2.get(0).getClienteid());
        
        verify(edutechRepository, times(1)).findByClienteid(cliente1);
        verify(edutechRepository, times(1)).findByClienteid(cliente2);
    }

    /**
     * Prueba la búsqueda de tickets por múltiples estados.
     * Verifica el comportamiento con diferentes estados de tickets.
     */
    @Test
    @DisplayName("Debe buscar tickets por diferentes estados")
    void testObtenerTicketsMultiplesEstados() {
        // Given
        String estadoAbierta = "ABIERTA";
        String estadoProceso = "EN_PROCESO";
        
        List<edutechmodel> ticketsAbiertos = Arrays.asList(ticketEjemplo);
        List<edutechmodel> ticketsProceso = Arrays.asList(listaTickets.get(1));
        
        when(edutechRepository.findByEstado(estadoAbierta)).thenReturn(ticketsAbiertos);
        when(edutechRepository.findByEstado(estadoProceso)).thenReturn(ticketsProceso);

        // When
        List<edutechmodel> resultadoAbiertos = edutechService.obtenerTicketsPorEstado(estadoAbierta);
        List<edutechmodel> resultadoProceso = edutechService.obtenerTicketsPorEstado(estadoProceso);

        // Then
        assertEquals(1, resultadoAbiertos.size());
        assertEquals(estadoAbierta, resultadoAbiertos.get(0).getEstado());
        
        assertEquals(1, resultadoProceso.size());
        assertEquals(estadoProceso, resultadoProceso.get(0).getEstado());
        
        verify(edutechRepository, times(1)).findByEstado(estadoAbierta);
        verify(edutechRepository, times(1)).findByEstado(estadoProceso);
    }
}