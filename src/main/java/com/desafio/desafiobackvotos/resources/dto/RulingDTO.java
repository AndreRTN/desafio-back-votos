package com.desafio.desafiobackvotos.resources.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RulingDTO {

    @NotNull(message = "Nome da pauta não deve estar nulo (name)")
    @NotEmpty(message = "Nome da pauta não deve estar vazio (name)")
    private String name;

    @Min(value = 1, message = "Tempo minimo para a pauta se encerrar é de 1 minuto (minutesToEnd)")
    private Integer minutesToEnd = 1;
}
