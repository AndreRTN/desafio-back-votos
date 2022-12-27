package com.desafio.desafiobackvotos.repository;

import com.desafio.desafiobackvotos.models.Ruling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RulingRepository  extends JpaRepository<Ruling, Long> {

    @Query(value = "SELECT * FROM Ruling ru " +
            "JOIN associate_ruling asr on ru.id = asr.ruling_id " +
            "JOIN  associate assoc on assoc.cpf = asr.associate_id", nativeQuery = true)
    List<Ruling> fetchRulingsWithAssociates();
    List<Ruling> findAllByIsOpenAndExpiratedNot(Boolean isOpen, Boolean expirated);
}
