package com.edutech.servicioalcliente.controller;

import com.edutech.servicioalcliente.model.edutechmodel;
import com.edutech.servicioalcliente.service.edutechservice;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para el controlador EdutechController.
 * 
 * Esta clase contiene todas las pruebas unitarias para verificar el comportamiento
 * correcto de los endpoints del controlador de tickets de soporte.
 * 
 * @author Sistema EduTech
 * @version 1.0
 */
@WebMvcTest(EdutechControllerTest.class)
@DisplayName("Pruebas unitarias para EdutechController")
class EdutechControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private edutechservice edutechService;

    @Autowired
    private ObjectMapper objectMapper;

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
     * Prueba el endpoint de ping/health check.
     * Verifica que el microservicio responde correctamente.
     */
    @Test
    @DisplayName("Debe responder correctamente al ping")
    void testPing() throws Exception {
        mockMvc.perform(get("/api/edutech/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("Microservicio Servicio al Cliente Activo"));
    }

    /**
     * Prueba la obtención de todos los tickets.
     * Verifica que retorna la lista completa de tickets con HATEOAS.
     */
    @Test
    @DisplayName("Debe obtener todos los tickets exitosamente")
    void testObtenerTodosLosTickets() throws Exception {
        when(edutechService.obtenerTodos()).thenReturn(listaTickets);

        mockMvc.perform(get("/api/edutech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.edutechmodelList").isArray())
                .andExpect(jsonPath("$._embedded.edutechmodelList.length()").value(2))
                .andExpect(jsonPath("$._embedded.edutechmodelList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.edutechmodelList[0].titulo").value("Problema con la plataforma"))
                .andExpect(jsonPath("$._embedded.edutechmodelList[1].id").value(2))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.create").exists());

        verify(edutechService, times(1)).obtenerTodos();
    }

    /**
     * Prueba la obtención de todos los tickets cuando la lista está vacía.
     */
    @Test
    @DisplayName("Debe manejar lista vacía de tickets")
    void testObtenerTodosLosTicketsVacio() throws Exception {
        when(edutechService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/edutech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links.self").exists());

        verify(edutechService, times(1)).obtenerTodos();
    }

    /**
     * Prueba la obtención de un ticket por ID cuando existe.
     * Verifica que retorna el ticket correcto con enlaces HATEOAS.
     */
    @Test
    @DisplayName("Debe obtener un ticket por ID cuando existe")
    void testObtenerTicketPorIdExistente() throws Exception {
        when(edutechService.obtenerPorId(1L)).thenReturn(Optional.of(ticketEjemplo));

        mockMvc.perform(get("/api/edutech/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Problema con la plataforma"))
                .andExpect(jsonPath("$.estado").value("ABIERTA"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.all-tickets").exists())
                .andExpect(jsonPath("$._links.update").exists())
                .andExpect(jsonPath("$._links.delete").exists());

        verify(edutechService, times(1)).obtenerPorId(1L);
    }

    /**
     * Prueba la obtención de un ticket por ID cuando no existe.
     * Verifica que lanza una excepción RuntimeException.
     */
    @Test
    @DisplayName("Debe lanzar excepción cuando el ticket no existe")
    void testObtenerTicketPorIdNoExistente() throws Exception {
        when(edutechService.obtenerPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/edutech/999"))
                .andExpect(status().isInternalServerError());

        verify(edutechService, times(1)).obtenerPorId(999L);
    }

    /**
     * Prueba la creación de un nuevo ticket.
     * Verifica que el ticket se crea correctamente y retorna código 201 con HATEOAS.
     */
    @Test
    @DisplayName("Debe crear un nuevo ticket exitosamente")
    void testCrearTicket() throws Exception {
        edutechmodel nuevoTicket = edutechmodel.builder()
                .titulo("Nuevo problema")
                .descripcion("Descripción del problema")
                .clienteid(102L)
                .build();

        edutechmodel ticketGuardado = edutechmodel.builder()
                .id(3L)
                .titulo("Nuevo problema")
                .descripcion("Descripción del problema")
                .estado("ABIERTA")
                .clienteid(102L)
                .fechacreacion(LocalDateTime.now())
                .build();

        when(edutechService.guardar(any(edutechmodel.class))).thenReturn(ticketGuardado);

        mockMvc.perform(post("/api/edutech")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoTicket)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.titulo").value("Nuevo problema"))
                .andExpect(jsonPath("$.estado").value("ABIERTA"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(header().exists("Location"));

        verify(edutechService, times(1)).guardar(any(edutechmodel.class));
    }

    /**
     * Prueba la actualización de un ticket existente.
     * Verifica que el ticket se actualiza correctamente con HATEOAS.
     */
    @Test
    @DisplayName("Debe actualizar un ticket existente")
    void testActualizarTicketExistente() throws Exception {
        edutechmodel ticketActualizado = edutechmodel.builder()
                .titulo("Título actualizado")
                .descripcion("Descripción actualizada")
                .estado("EN_PROCESO")
                .clienteid(100L)
                .build();

        edutechmodel ticketExistente = edutechmodel.builder()
                .id(1L)
                .titulo("Título actualizado")
                .descripcion("Descripción actualizada")
                .estado("EN_PROCESO")
                .clienteid(100L)
                .fechacreacion(LocalDateTime.now())
                .build();

        when(edutechService.obtenerPorId(1L)).thenReturn(Optional.of(ticketEjemplo));
        when(edutechService.guardar(any(edutechmodel.class))).thenReturn(ticketExistente);

        mockMvc.perform(put("/api/edutech/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Título actualizado"))
                .andExpect(jsonPath("$._links.self").exists());

        verify(edutechService, times(1)).obtenerPorId(1L);
        verify(edutechService, times(1)).guardar(any(edutechmodel.class));
    }

    /**
     * Prueba la actualización de un ticket que no existe.
     * Verifica que retorna código 404.
     */
    @Test
    @DisplayName("Debe retornar 404 al actualizar ticket inexistente")
    void testActualizarTicketNoExistente() throws Exception {
        edutechmodel ticketActualizado = edutechmodel.builder()
                .titulo("Título actualizado")
                .descripcion("Descripción actualizada")
                .estado("EN_PROCESO")
                .clienteid(100L)
                .build();

        when(edutechService.obtenerPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/edutech/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketActualizado)))
                .andExpect(status().isNotFound());

        verify(edutechService, times(1)).obtenerPorId(999L);
        verify(edutechService, never()).guardar(any(edutechmodel.class));
    }

    /**
     * Prueba la eliminación de un ticket existente.
     * Verifica que el ticket se elimina correctamente y retorna código 204.
     */
    @Test
    @DisplayName("Debe eliminar un ticket existente")
    void testEliminarTicketExistente() throws Exception {
        when(edutechService.obtenerPorId(1L)).thenReturn(Optional.of(ticketEjemplo));
        doNothing().when(edutechService).eliminar(1L);

        mockMvc.perform(delete("/api/edutech/1"))
                .andExpect(status().isNoContent());

        verify(edutechService, times(1)).obtenerPorId(1L);
        verify(edutechService, times(1)).eliminar(1L);
    }

    /**
     * Prueba la eliminación de un ticket que no existe.
     * Verifica que retorna código 404.
     */
    @Test
    @DisplayName("Debe retornar 404 al eliminar ticket inexistente")
    void testEliminarTicketNoExistente() throws Exception {
        when(edutechService.obtenerPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/edutech/999"))
                .andExpect(status().isNotFound());

        verify(edutechService, times(1)).obtenerPorId(999L);
        verify(edutechService, never()).eliminar(anyLong());
    }

    /**
     * Prueba la obtención de tickets por cliente.
     * Verifica que retorna los tickets del cliente especificado con HATEOAS.
     */
    @Test
    @DisplayName("Debe obtener tickets por cliente")
    void testObtenerTicketsPorCliente() throws Exception {
        List<edutechmodel> ticketsCliente = Arrays.asList(ticketEjemplo);
        when(edutechService.obtenerTicketsPorCliente(100L)).thenReturn(ticketsCliente);

        mockMvc.perform(get("/api/edutech/cliente/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.edutechmodelList.length()").value(1))
                .andExpect(jsonPath("$._embedded.edutechmodelList[0].clienteid").value(100))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.all-tickets").exists());

        verify(edutechService, times(1)).obtenerTicketsPorCliente(100L);
    }

    /**
     * Prueba la obtención de tickets por cliente cuando no hay tickets.
     * Verifica que retorna código 204 (No Content).
     */
    @Test
    @DisplayName("Debe retornar 204 cuando no hay tickets para el cliente")
    void testObtenerTicketsPorClienteSinTickets() throws Exception {
        when(edutechService.obtenerTicketsPorCliente(999L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/edutech/cliente/999"))
                .andExpect(status().isNoContent());

        verify(edutechService, times(1)).obtenerTicketsPorCliente(999L);
    }

    /**
     * Prueba el cambio de estado de un ticket.
     * Verifica que el estado se actualiza correctamente con HATEOAS.
     */
    @Test
    @DisplayName("Debe cambiar el estado de un ticket")
    void testCambiarEstadoTicket() throws Exception {
        edutechmodel ticketConEstadoCambiado = edutechmodel.builder()
                .id(1L)
                .titulo("Problema con la plataforma")
                .descripcion("No puedo acceder a mi curso")
                .estado("CERRADA")
                .clienteid(100L)
                .fechacreacion(LocalDateTime.now())
                .build();

        when(edutechService.obtenerPorId(1L)).thenReturn(Optional.of(ticketEjemplo));
        when(edutechService.guardar(any(edutechmodel.class))).thenReturn(ticketConEstadoCambiado);

        mockMvc.perform(patch("/api/edutech/1/estado")
                        .param("nuevoEstado", "CERRADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CERRADA"))
                .andExpect(jsonPath("$._links.self").exists());

        verify(edutechService, times(1)).obtenerPorId(1L);
        verify(edutechService, times(1)).guardar(any(edutechmodel.class));
    }

    /**
     * Prueba el cambio de estado de un ticket que no existe.
     * Verifica que retorna código 404.
     */
    @Test
    @DisplayName("Debe retornar 404 al cambiar estado de ticket inexistente")
    void testCambiarEstadoTicketNoExistente() throws Exception {
        when(edutechService.obtenerPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/edutech/999/estado")
                        .param("nuevoEstado", "CERRADA"))
                .andExpect(status().isNotFound());

        verify(edutechService, times(1)).obtenerPorId(999L);
        verify(edutechService, never()).guardar(any(edutechmodel.class));
    }

    /**
     * Prueba la obtención de tickets por estado.
     * Verifica que retorna los tickets con el estado especificado con HATEOAS.
     */
    @Test
    @DisplayName("Debe obtener tickets por estado")
    void testObtenerTicketsPorEstado() throws Exception {
        List<edutechmodel> ticketsAbiertos = Arrays.asList(ticketEjemplo);
        when(edutechService.obtenerTicketsPorEstado("ABIERTA")).thenReturn(ticketsAbiertos);

        mockMvc.perform(get("/api/edutech/estado/ABIERTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.edutechmodelList.length()").value(1))
                .andExpect(jsonPath("$._embedded.edutechmodelList[0].estado").value("ABIERTA"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.all-tickets").exists());

        verify(edutechService, times(1)).obtenerTicketsPorEstado("ABIERTA");
    }

    /**
     * Prueba la obtención de tickets por estado cuando no hay tickets.
     * Verifica que retorna código 204 (No Content).
     */
    @Test
    @DisplayName("Debe retornar 204 cuando no hay tickets con el estado especificado")
    void testObtenerTicketsPorEstadoSinTickets() throws Exception {
        when(edutechService.obtenerTicketsPorEstado("CERRADA")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/edutech/estado/CERRADA"))
                .andExpect(status().isNoContent());

        verify(edutechService, times(1)).obtenerTicketsPorEstado("CERRADA");
    }

    /**
     * Prueba el manejo de errores en la creación de tickets con datos inválidos.
     */
    @Test
    @DisplayName("Debe manejar errores en la creación con datos inválidos")
    void testCrearTicketConDatosInvalidos() throws Exception {
        // Ticket sin título (requerido)
        edutechmodel ticketInvalido = edutechmodel.builder()
                .descripcion("Descripción sin título")
                .clienteid(102L)
                .build();

        mockMvc.perform(post("/api/edutech")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketInvalido)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Prueba que el endpoint de cambio de estado convierte a mayúsculas.
     */
    @Test
    @DisplayName("Debe convertir estado a mayúsculas al cambiar estado")
    void testCambiarEstadoConvierteAMayusculas() throws Exception {
        edutechmodel ticketConEstadoCambiado = edutechmodel.builder()
                .id(1L)
                .titulo("Problema con la plataforma")
                .descripcion("No puedo acceder a mi curso")
                .estado("CERRADA")
                .clienteid(100L)
                .fechacreacion(LocalDateTime.now())
                .build();

        when(edutechService.obtenerPorId(1L)).thenReturn(Optional.of(ticketEjemplo));
        when(edutechService.guardar(any(edutechmodel.class))).thenReturn(ticketConEstadoCambiado);

        mockMvc.perform(patch("/api/edutech/1/estado")
                        .param("nuevoEstado", "cerrada")) // minúsculas
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADA")); // debe convertir a mayúsculas

        verify(edutechService, times(1)).obtenerPorId(1L);
        verify(edutechService, times(1)).guardar(any(edutechmodel.class));
    }
}