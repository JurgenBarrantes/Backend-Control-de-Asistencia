package com.systems.service;

import com.systems.model.TardinessRule;

import java.util.Optional;

public interface ITardinessRuleService extends IGenericService<TardinessRule, Integer> {
    Optional<TardinessRule> findByClassroomId(Integer classroomId);
}
