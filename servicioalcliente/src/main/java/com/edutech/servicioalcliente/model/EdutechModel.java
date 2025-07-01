package com.edutech.servicioalcliente.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "edutech_tabla")
@Builder
public class EdutechModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_seq")
    @SequenceGenerator(name = "ticket_seq", sequenceName = "TICKET_SEQ", allocationSize = 1)
    @Column(name = "id")
    private long id;
    
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "estado", nullable = false, length = 50)
    private String estado;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechacreacion;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteid;
    
    @PrePersist
    protected void onCreate() {
        if (fechacreacion == null) {
            fechacreacion = LocalDateTime.now();
        }
        if (estado == null || estado.isEmpty()) {
            estado = "ABIERTA";
        }
    }
}
