package com.systems.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "schedules")
public class Schedule { // para definir los horarios de las clases
    @Id
    // TEMPORAL: @GeneratedValue comentado hasta que se configure AUTO_INCREMENT en la DB
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idSchedule;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "id_classroom", nullable = true) // TEMPORAL: permitir null para facilitar pruebas
    private Classroom classroom;

    

    /*@ManyToOne
    @JoinColumn(name = "id_subject", nullable = false)
    private Subject subject;*/


}
