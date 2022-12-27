package com.desafio.desafiobackvotos.resources.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequestDTO {

    @NotNull(message = "Você deve votar Sim ou Não! (positiveVote)")
    private Boolean positiveVote;

    @NotNull(message = "Digite um cpf (cpf)")
    private String cpf;
}
