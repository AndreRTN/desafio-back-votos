package com.desafio.desafiobackvotos.resources.dto;


import lombok.Data;

@Data
public class RulingResultResponseDTO {
    private String name;
    private Long total;
    private Long negativeVote;
    private Long positiveVote;
    private Boolean expirated;
}
