package com.desafio.desafiobackvotos.units.service;


import com.desafio.desafiobackvotos.common.exceptions.AssociateAlreadyVotedException;
import com.desafio.desafiobackvotos.common.exceptions.RulingExpiratedException;
import com.desafio.desafiobackvotos.common.exceptions.RulingNotFoundException;
import com.desafio.desafiobackvotos.common.exceptions.RulingNotOpenedException;
import com.desafio.desafiobackvotos.common.type.VoteResultType;
import com.desafio.desafiobackvotos.config.WebClientConfig;
import com.desafio.desafiobackvotos.models.Associate;
import com.desafio.desafiobackvotos.models.Ruling;
import com.desafio.desafiobackvotos.repository.AssociateRepository;
import com.desafio.desafiobackvotos.repository.RulingRepository;
import com.desafio.desafiobackvotos.resources.dto.RulingDTO;
import com.desafio.desafiobackvotos.resources.dto.RulingResultResponseDTO;
import com.desafio.desafiobackvotos.resources.dto.VoteRequestDTO;
import com.desafio.desafiobackvotos.services.RulingService;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class RulingServiceTest {

    @Mock
    RulingRepository rulingRepository;

    @Mock
    AssociateRepository associateRepository;
    @InjectMocks
    RulingService rulingService;

    private RulingDTO rulingDTO;

    private Ruling rulingModel;
    private Associate associateModel;

    private VoteRequestDTO voteRequestDTO;

    @Before
    public void mockAll() {
        rulingDTO = new RulingDTO();
        rulingDTO.setName("Pauta Mockada");
        Ruling ruling = new Ruling();
        ruling.setMinutesToEnd(1);
        ruling.setName("Pauta Mockada");
        ruling.setId(1L);
        rulingModel = ruling;

        Associate associate = new Associate();
        String cpf = "359.589.100-78";
        associate.setCpf(cpf);
        associateModel = associate;

        VoteRequestDTO voteRequest = new VoteRequestDTO();
        voteRequest.setCpf(cpf);
        voteRequest.setPositiveVote(true);
        voteRequestDTO = voteRequest;
    }


    @Test
    @DisplayName("Deve criar uma pauta e retornar o objeto salvo")
    public void shouldCreateRulingAndReturn() {
        Ruling ruling = rulingService.save(rulingDTO);
        Mockito.verify(rulingRepository, Mockito.times(1)).save(ruling);
        Assert.assertEquals(ruling.getName(), rulingModel.getName());

    }

    @Test
    @DisplayName("Deve receber o id da pauta e abrir uma sessão de votação")
    public void  shouldReceiveIdAndOpenRuling() {
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Ruling ruling = rulingService.openRuling(1L);
        Assert.assertEquals(ruling.getIsOpen(), true);
        Assert.assertTrue(ruling.getExpired_date().isAfter(LocalDateTime.now()));
        Mockito.verify(rulingRepository, Mockito.times(1)).save(ruling);
    }

    @Test
    @DisplayName("Quando não encontrar a pauta deve lançar not found exception")
    public void whenNotFoudRulingShouldThrowNotFoundException() {
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.empty());
        Assert.assertThrows(RulingNotFoundException.class,() -> rulingService.openRuling(1L));
    }

    @Test
    @DisplayName("Quando a pauta encontrada já estiver encerrada, deve lançar expired exception")
    public void whenRulingHasExpiredShouldThrowExpiredException() {
        rulingModel.setExpirated(true);
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Assert.assertThrows(RulingExpiratedException.class, () -> rulingService.openRuling(1L));
    }

    @Test
    @DisplayName("Se a pauta já estiver aberta para votação, deve apenas retornar o objeto encontrado no banco")
    public void whenRulingIsOpenThenReturnIt() {
        rulingModel.setIsOpen(true);
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Ruling ruling = rulingService.openRuling(1L);
        Assert.assertEquals(ruling, rulingModel);
        Mockito.verify(rulingRepository, Mockito.times(0)).save(ruling);
    }

    @Test
    @DisplayName("Deve retornar o resultado da votação na pauta")
    public void shouldReturnRulingResult() {
        rulingModel.setTotal(100L);
        rulingModel.setPositiveVote(50L);
        rulingModel.setNegativeVote(50L);
        rulingModel.setIsOpen(true);
        rulingModel.setExpirated(true);
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        RulingResultResponseDTO responseDTO = rulingService.rulingResult(1L);
        Assert.assertEquals(rulingModel.getName(), responseDTO.getName());
        Assert.assertEquals(rulingModel.getTotal(), responseDTO.getTotal());
        Assert.assertEquals(rulingModel.getNegativeVote(), responseDTO.getNegativeVote());
        Assert.assertEquals(rulingModel.getPositiveVote(), responseDTO.getPositiveVote());
        Assert.assertEquals(responseDTO.getResult(), VoteResultType.LOSE);
    }

    @Test
    @DisplayName("Se a pauta ainda não foi aberta a sessão de votação, deve lançar uma exceção")
    public void shouldThrowNotOpenedException() {
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Assert.assertThrows(RulingNotOpenedException.class, () -> rulingService.rulingResult(1L));
    }

    @Test
    @DisplayName("Deve ser possível um associado votar em uma pauta aberta para votação")
    public void shouldAssociateVoteInRuling() {
        rulingModel.setIsOpen(true);
        rulingModel.setExpired_date(LocalDateTime.now().plusMinutes(rulingModel.getMinutesToEnd()));

        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Mockito.when(associateRepository.findById(voteRequestDTO.getCpf())).thenReturn(Optional.ofNullable(associateModel));
        Ruling ruling = rulingService.vote(voteRequestDTO, 1L);
        Assert.assertEquals(1L, (long) ruling.getTotal());
        Assert.assertEquals(0L, (long) ruling.getNegativeVote());
        Assert.assertEquals(1L, (long) ruling.getPositiveVote());
        Assert.assertEquals(associateModel.getRulings().size(), 1);
        Mockito.verify(associateRepository, Mockito.times(1)).save(associateModel);
    }

    @Test
    @DisplayName("Ao tentar votar em uma pauta que não existe, deve lançar not found exception")
    public void whenVoteInNotFoundRulingShouldThrowException() {
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.empty());
        Assert.assertThrows(RulingNotFoundException.class, () -> rulingService.vote(voteRequestDTO, 1L));
    }

    @Test
    @DisplayName("Ao tentar votar em uma pauta não aberta para votação, deve lançar not opened exception")
    public void whenVoteInCloseRulingShouldThrowNotOpenedException() {
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Assert.assertThrows(RulingNotOpenedException.class, () -> rulingService.vote(voteRequestDTO,1L));
    }

    @Test
    @DisplayName("Ao tentar votar em uma pauta encerrada, deve lançar not expired exception")
    public void whenVoteInExpiredRulingShouldThrowExpiredException() {
        rulingModel.setExpirated(true);
        rulingModel.setIsOpen(true);
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Assert.assertThrows(RulingExpiratedException.class, () -> rulingService.vote(voteRequestDTO,1L));
    }

    @Test
    @DisplayName("Se o associado já votou na pauta, deve lançar already voted exception")
    public void whenAssociateHasVotedInRulingShouldThrowAlreadyVotedException() {
        rulingModel.setIsOpen(true);
        rulingModel.setExpirated(false);
        rulingModel.setExpired_date(LocalDateTime.now().plusMinutes(rulingModel.getMinutesToEnd()));
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.ofNullable(rulingModel));
        Mockito.when(associateRepository.alreadyVotedRuling(1L , voteRequestDTO.getCpf())).thenReturn(true);
        Assert.assertThrows(AssociateAlreadyVotedException.class, () -> rulingService.vote(voteRequestDTO, 1L));
    }
}

