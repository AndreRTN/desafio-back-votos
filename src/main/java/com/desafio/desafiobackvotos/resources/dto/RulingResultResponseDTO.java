package com.desafio.desafiobackvotos.resources.dto;


import com.desafio.desafiobackvotos.common.type.VoteResultType;
import lombok.Data;

@Data
public class RulingResultResponseDTO {
    private String name;
    private Long total;
    private Long negativeVote;
    private Long positiveVote;
    private Boolean expirated;
    private VoteResultType result;
}
