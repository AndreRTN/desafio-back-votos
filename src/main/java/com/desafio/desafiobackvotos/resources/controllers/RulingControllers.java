package com.desafio.desafiobackvotos.resources.controllers;

import com.desafio.desafiobackvotos.common.exceptions.*;
import com.desafio.desafiobackvotos.common.pojo.ErrorRestResult;
import com.desafio.desafiobackvotos.common.type.VoteType;
import com.desafio.desafiobackvotos.config.WebClientConfig;
import com.desafio.desafiobackvotos.models.Ruling;
import com.desafio.desafiobackvotos.resources.dto.RulingDTO;
import com.desafio.desafiobackvotos.resources.dto.RulingResultResponseDTO;
import com.desafio.desafiobackvotos.resources.dto.VoteRequestDTO;
import com.desafio.desafiobackvotos.resources.dto.VoteResponseDTO;
import com.desafio.desafiobackvotos.services.RulingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/rulings")
@Slf4j
public class RulingControllers {

    private final RulingService rulingService;
    private final WebClient webClient;
    @Autowired
    public RulingControllers(RulingService rulingService, WebClientConfig config) {
        this.rulingService = rulingService;
        this.webClient = config.webClient();
    }

    @PutMapping("open/{id}")
    private Mono<Ruling> openRuling(@PathVariable Long id) {
        Ruling openedRuling = rulingService.openRuling(id);
        return Mono.just(openedRuling);
    };

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "RULING HAS EXPIRED")
    @ExceptionHandler(RulingExpiratedException.class)
    public ResponseEntity<ErrorRestResult> RulingExpiratedException(RulingExpiratedException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(new ErrorRestResult(HttpStatus.BAD_REQUEST.value(), exception.getMessage()), HttpStatus.BAD_REQUEST);

    }

    @PutMapping("vote/{id}")
    private Mono<VoteResponseDTO> vote(@PathVariable Long id, @Valid @RequestBody VoteRequestDTO dto) {

        return webClient.get().uri("/users/" + dto.getCpf()).exchangeToMono(res -> {
            if(res.statusCode().is4xxClientError()) throw  new UnableToVoteException(dto.getCpf());
            String voteType = dto.getPositiveVote() ? "Positivo" : "Negativo";
            Ruling ruling = rulingService.vote(dto, id);
            String message = String.format("Voto %s realizado na Pauta: %s", voteType, ruling.getName());
            return Mono.just(new VoteResponseDTO(message));
        });
    }

    @ExceptionHandler(RulingNotOpenedException.class)
    public ResponseEntity<ErrorRestResult> RulingNotOpenedHandler(RulingNotOpenedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorRestResult(HttpStatus.BAD_REQUEST.value(), exception.getMessage()), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(UnableToVoteException.class)
    public ResponseEntity<ErrorRestResult> UnableToVoteHandler(UnableToVoteException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorRestResult(HttpStatus.BAD_REQUEST.value(), VoteType.UNABLE_TO_VOTE.name()), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(AssociateAlreadyVotedException.class)
    public ResponseEntity<ErrorRestResult> AssociateAlreadyVotedHandler(AssociateAlreadyVotedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorRestResult(HttpStatus.BAD_REQUEST.value(), exception.getMessage()), HttpStatus.BAD_REQUEST);

    }

    @GetMapping("all")
    private Mono<List<Ruling>> listRulings() {
        return Mono.just(rulingService.listAll());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "RULING NOT FOUND")
    @ExceptionHandler(RulingNotFoundException.class)
    public ResponseEntity<Object> URLNotFoundHandler(RulingNotFoundException exception) {
        log.error(exception.getMessage());

        return new ResponseEntity<>(new ErrorRestResult(HttpStatus.NOT_FOUND.value(), exception.getMessage()), HttpStatus.NOT_FOUND);

    }

    @GetMapping("result/{id}")
    private Mono<RulingResultResponseDTO> rulingResult(@PathVariable Long id) {
        return Mono.just(rulingService.rulingResult(id));
    }

    @PostMapping
    private Mono<Ruling > save( @Valid @RequestBody RulingDTO dto) {
        return Mono.just(rulingService.save(dto));
    }
}
