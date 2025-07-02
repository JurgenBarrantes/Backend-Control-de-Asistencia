package com.systems.service.impl;

import org.springframework.stereotype.Service;

import com.systems.model.Assist;
import com.systems.repo.IAssistRepo;
import com.systems.repo.IGenericRepo;
import com.systems.service.IAssistService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssistService extends GenericService<Assist, Integer> implements IAssistService {
    
    private final IAssistRepo repo;

    @Override
    protected IGenericRepo<Assist, Integer> getRepo() {
        return repo;
    }
}
