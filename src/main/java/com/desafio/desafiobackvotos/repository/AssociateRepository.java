package com.desafio.desafiobackvotos.repository;

import com.desafio.desafiobackvotos.models.Associate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssociateRepository  extends JpaRepository<Associate, String> {

    @Query(value = "SELECT CASE WHEN COUNT(ru) > 0 THEN true ELSE false END " +
            "FROM Ruling ru " +
            "INNER JOIN associate_ruling aru on ru.id = aru.ruling_id " +
            "inner join associate a on a.cpf = aru.associate_id " +
            "where ru.id = :rulingId and a.cpf = :associateId ", nativeQuery = true)
    Boolean alreadyVotedRuling(@Param("rulingId") Long rullingId, @Param("associateId") String associateId);

}
