package com.desafio.desafiobackvotos.units.controllers;


import com.desafio.desafiobackvotos.common.exceptions.AssociateAlreadyVotedException;
import com.desafio.desafiobackvotos.common.exceptions.RulingNotOpenedException;
import com.desafio.desafiobackvotos.common.type.VoteResultType;
import com.desafio.desafiobackvotos.config.WebClientConfig;
import com.desafio.desafiobackvotos.models.Associate;
import com.desafio.desafiobackvotos.models.Ruling;
import com.desafio.desafiobackvotos.repository.RulingRepository;
import com.desafio.desafiobackvotos.resources.controllers.RulingController;
import com.desafio.desafiobackvotos.resources.dto.RulingDTO;
import com.desafio.desafiobackvotos.resources.dto.RulingResultResponseDTO;
import com.desafio.desafiobackvotos.resources.dto.VoteRequestDTO;
import com.desafio.desafiobackvotos.services.RulingService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = RulingController.class)

public class RulingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WebClient webClient;

    @MockBean
    private RulingService service;

    private RulingDTO rulingDTO;

    private Ruling rulingModel;

    @MockBean
    private RulingRepository rulingRepository;

    private RulingResultResponseDTO responseDTO;


    @BeforeEach
    public void mockAll() {

        rulingDTO = new RulingDTO();
        rulingDTO.setName("Pauta Mockada");
        Ruling ruling = new Ruling();
        ruling.setMinutesToEnd(1);
        ruling.setName("Pauta Mockada");
        ruling.setId(1L);
        rulingModel = ruling;


        RulingResultResponseDTO dto = new RulingResultResponseDTO();
        BeanUtils.copyProperties(ruling, dto);
        VoteResultType voteResultType  = ruling.getPositiveVote() > ruling.getNegativeVote() ? VoteResultType.WIN : VoteResultType.LOSE;
        dto.setResult(voteResultType);
        responseDTO = dto;
    }

    @Test
    public void shouldSaveRuling() {
        Mockito.when(service.save(rulingDTO)).thenReturn(rulingModel);
        webTestClient.post().uri("/rulings").bodyValue(rulingDTO)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ruling.class)
                .isEqualTo(rulingModel);

    }

    @Test
    public void shouldReturnResultRuling() {
        Mockito.when(service.rulingResult(1L)).thenReturn(responseDTO);
        webTestClient.get().uri("/rulings/result/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RulingResultResponseDTO.class)
                .isEqualTo(responseDTO);
    }

    @Test
    public void shouldOpenRuling() {
        Mockito.when(service.openRuling(1L)).thenReturn(rulingModel);
        webTestClient.put().uri("/rulings/open/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Ruling.class)
                .isEqualTo(rulingModel);

    }

    @Test
    public void shouldReturnNotFoundIfRulingNotExists() {
        Mockito.when(rulingRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(service.openRuling(1L)).thenThrow(RulingNotOpenedException.class);
        webTestClient.put().uri("/rulings/open/1")
                .exchange()
                .expectStatus().isBadRequest();

    }



}
