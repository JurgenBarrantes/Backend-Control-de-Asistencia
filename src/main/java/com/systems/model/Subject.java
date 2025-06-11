package com.systems.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "subjects")
public class Subject { // representa una asignatura o materia en el sistema
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idSubject;

    @Column(nullable = false, length = 60)
    private String name;

    /*
     * @ManyToOne
     * 
     * @JoinColumn(name = "id_teacher", nullable = false)
     * private Teacher teacher; // profesor que imparte la asignatura
     */

}
