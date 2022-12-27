package com.desafio.desafiobackvotos.services;


import com.desafio.desafiobackvotos.common.exceptions.AssociateAlreadyVotedException;
import com.desafio.desafiobackvotos.common.exceptions.RulingExpiratedException;
import com.desafio.desafiobackvotos.common.exceptions.RulingNotFoundException;
import com.desafio.desafiobackvotos.common.exceptions.RulingNotOpenedException;
import com.desafio.desafiobackvotos.models.Associate;
import com.desafio.desafiobackvotos.models.Ruling;
import com.desafio.desafiobackvotos.repository.AssociateRepository;
import com.desafio.desafiobackvotos.repository.RulingRepository;
import com.desafio.desafiobackvotos.resources.dto.RulingDTO;
import com.desafio.desafiobackvotos.resources.dto.RulingResultResponseDTO;
import com.desafio.desafiobackvotos.resources.dto.VoteRequestDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RulingService {

    private RulingRepository rulingRepository;
    private AssociateRepository associateRepository;

    @Autowired
    public RulingService(RulingRepository rulingRepository, AssociateRepository associateRepository) {
        this.rulingRepository = rulingRepository;
        this.associateRepository = associateRepository;
    }

    public Ruling save(RulingDTO dto) {
        Ruling ruling = convertDTOToModel(dto);
        rulingRepository.save(ruling);
        return ruling;
    }

    private void expiresRuling(Ruling ruling) {

    }


    public Ruling openRuling(Long id) {
        Ruling ruling = rulingRepository.findById(id).orElseThrow(() -> new RulingNotFoundException(id));
        if(ruling.getExpirated()) throw new RulingExpiratedException(id);
        if(ruling.getIsOpen()) return ruling;
        ruling.setIsOpen(true);
        ruling.setExpired_date(LocalDateTime.now().plusMinutes(ruling.getMinutesToEnd()));
        rulingRepository.save(ruling);
        return ruling;
    }

    @Transactional
    public Ruling vote(VoteRequestDTO dto, Long id ) {
        Optional<Associate> hasAssociate = associateRepository.findById(dto.getCpf());
        Ruling ruling = rulingRepository.findById(id).orElseThrow(() -> new RulingNotFoundException(id));
        if(!ruling.getIsOpen() && !ruling.getExpirated()) throw new RulingNotOpenedException();
        if(ruling.getExpirated()) throw  new RulingExpiratedException(id);

        Boolean alreadyVoted = associateRepository.alreadyVotedRuling(id, dto.getCpf());
        if(alreadyVoted) throw new AssociateAlreadyVotedException(ruling.getName());

        ruling.addVote(dto.getPositiveVote());
        hasAssociate.ifPresentOrElse(associate -> {
            associate.addRuling(ruling);
            associateRepository.save(associate);
        }, () -> {
            Associate associate = new Associate();
            associate.setCpf(dto.getCpf());
            associate.setVoted_at(LocalDateTime.now());
            associate.addRuling(ruling);
            associateRepository.save(associate);
        });

        return ruling;


    }
    public List<Ruling> listAll() {
        return rulingRepository.findAll();
    }

    // value in ms, multiply minute value by 60000 to convert in minutes
    @Scheduled(fixedDelay = 60000)
    @Async
    public void checkRulingSession() {
        List<Ruling> openRulings = rulingRepository.findAllByIsOpenAndExpiratedNot(true, true);
        List<Ruling> expiratedRulings = openRulings.stream()
                .filter(ruling -> ruling.getExpired_date().isBefore(LocalDateTime.now()))
                .toList();
        expiratedRulings.forEach(rul -> {
            rul.setIsOpen(false);
            rul.setExpirated(true);

        });
        rulingRepository.saveAll(expiratedRulings);
    }

    public Ruling convertDTOToModel(RulingDTO dto) {
        Ruling ruling = new Ruling();
        BeanUtils.copyProperties(dto, ruling);
        ruling.setCreated_at(LocalDateTime.now());
        return ruling;
    }


    public RulingResultResponseDTO rulingResult(Long id) {
        Ruling ruling = rulingRepository.findById(id).orElseThrow(() -> new RulingNotFoundException(id));
        if(!ruling.getIsOpen() && !ruling.getExpirated()) throw new RulingNotOpenedException("A Pauta ainda não foi aberta para votação, não há contagem de votos no momento");
        RulingResultResponseDTO dto = new RulingResultResponseDTO();
        BeanUtils.copyProperties(ruling, dto);

        return dto;
    }
}
