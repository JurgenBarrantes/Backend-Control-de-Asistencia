package com.systems.service.impl;

import org.springframework.stereotype.Service;

import com.systems.model.TardinessRule;
import com.systems.repo.IGenericRepo;
import com.systems.repo.ITardinessRuleRepo;
import com.systems.service.ITardinessRuleService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TardinessRuleService extends GenericService<TardinessRule, Integer> implements ITardinessRuleService {

    private final ITardinessRuleRepo repo;

    @Override
    protected IGenericRepo<TardinessRule, Integer> getRepo() {
        return repo;
    }

    @Override
    public Optional<TardinessRule> findByClassroomId(Integer classroomId) {
        return repo.findByClassroom_IdClassroom(classroomId);
    }
}
