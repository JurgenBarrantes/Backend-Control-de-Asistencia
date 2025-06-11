package com.systems.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tardiness_rules")
public class TardinessRule {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer idTardinessRule;

    @Column(nullable = false)
    private Integer tardinnessThresholdMinutes =10; // in minutes

    @Column(nullable = false)
    private Integer absenceThresholdMinutes = 30; // in minutes

    @ManyToOne
    @JoinColumn(name = "id_classroom", foreignKey = @ForeignKey(name = "FK_RUL_CLASSROOM"))
    private Classroom classroom;
}
