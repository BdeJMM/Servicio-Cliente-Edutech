package com.edutech.servicioalcliente.assembler;

import com.edutech.servicioalcliente.model.EdutechModel;
import com.edutech.servicioalcliente.controller.EdutechController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EdutechModelAssembler implements RepresentationModelAssembler<EdutechModel, EntityModel<EdutechModel>> {
    @Override
    @NonNull
    public EntityModel<EdutechModel> toModel(@NonNull EdutechModel ticket) {
        return EntityModel.of(ticket,
                linkTo(methodOn(EdutechController.class).obtenerTicketPorId(ticket.getId())).withSelfRel(),
                linkTo(methodOn(EdutechController.class).obtenerTodosLosTickets()).withRel("Ver todos los tickets"),
                linkTo(methodOn(EdutechController.class).actualizarTicket(ticket.getId(), ticket)).withRel("Actualizar"),
                linkTo(methodOn(EdutechController.class).eliminarTicket(ticket.getId())).withRel("Eliminar"),
                linkTo(methodOn(EdutechController.class).cambiarEstadoTicket(ticket.getId(), "ESTADO")).withRel("Modificar estado"),
                linkTo(methodOn(EdutechController.class).obtenerTicketsPorCliente(ticket.getClienteid())).withRel("Buscar por id de cliente"),
                linkTo(methodOn(EdutechController.class).obtenerTicketsPorEstado(ticket.getEstado())).withRel("Buscar por estado")
        );
    }
}
