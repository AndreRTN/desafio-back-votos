package com.desafio.desafiobackvotos.services;


import com.desafio.desafiobackvotos.repository.AssociateRepository;
import com.desafio.desafiobackvotos.repository.RulingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssociateService {

    private RulingRepository rulingRepository;
    private AssociateRepository associateRepository;

    @Autowired
    public AssociateService(RulingRepository rulingRepository, AssociateRepository associateRepository) {
        this.rulingRepository = rulingRepository;
        this.associateRepository = associateRepository;
    }


}
