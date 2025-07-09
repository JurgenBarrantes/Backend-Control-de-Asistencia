package com.systems.repo;

import com.systems.model.TardinessRule;

import java.util.Optional;

public interface ITardinessRuleRepo extends IGenericRepo<TardinessRule, Integer> {
    Optional<TardinessRule> findByClassroom_IdClassroom(Integer classroomId);
}
