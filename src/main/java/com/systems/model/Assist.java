package com.systems.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
@Table(name = "assists")
public class Assist {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idAssist;
    
    @Column(nullable = false, length = 60)
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "id_attendance", nullable = false)
    private Attendance attendance;
    
    @ManyToOne
    @JoinColumn(name = "id_enrollment", nullable = false)
    private Enrollment enrollment;

}
