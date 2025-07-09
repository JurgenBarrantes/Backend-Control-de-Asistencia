package com.systems.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "attendances")
public class Attendance { // para el control de asistencia de los estudiantes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idAttendance;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime entryTime;

    @Column(nullable = false)
    private boolean isPresent;

    @Column(nullable = false)
    private boolean isLate;

    @ManyToOne
    @JoinColumn(name = "id_classroom", nullable = false)
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "id_schedule", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

}
