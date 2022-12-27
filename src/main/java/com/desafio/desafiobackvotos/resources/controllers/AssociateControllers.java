package com.desafio.desafiobackvotos.resources.controllers;


import com.desafio.desafiobackvotos.common.pojo.ErrorRestResult;
import com.desafio.desafiobackvotos.common.type.VoteType;
import com.desafio.desafiobackvotos.config.WebClientConfig;
import com.desafio.desafiobackvotos.resources.dto.ValidCPFResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("associate")
public class AssociateControllers {

    private WebClient webClient;

    @Autowired
    public AssociateControllers(WebClientConfig config) {
        this.webClient = config.webClient();
    }

    @GetMapping("check/{cpf}")
    private Mono<ResponseEntity<Object>> checkCpf(@PathVariable String cpf) {

        Mono<ResponseEntity<Object>> responseSpec = webClient.get().uri("/users/" + cpf)
                .exchangeToMono(response -> {
                    if(response.statusCode().is4xxClientError()) {
                        ErrorRestResult errorRestResult = new ErrorRestResult(HttpStatus.NOT_FOUND.value(),"CPF inválido");
                        return Mono.just(new ResponseEntity<>(errorRestResult, HttpStatus.NOT_FOUND));
                    }

                    if(response.statusCode().is2xxSuccessful()) {
                        ValidCPFResponseDTO validCPFResponseDTO = new ValidCPFResponseDTO();
                        validCPFResponseDTO.setMessage(VoteType.ABLE_TO_VOTE);
                        return Mono.just(new ResponseEntity<>(validCPFResponseDTO, HttpStatus.ACCEPTED));
                    }
                    return Mono.just(new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR));
                });


        return responseSpec;


    }
}
