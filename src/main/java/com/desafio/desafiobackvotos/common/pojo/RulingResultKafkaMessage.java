package com.desafio.desafiobackvotos.common.pojo;


import com.desafio.desafiobackvotos.common.type.VoteResultType;
import lombok.Data;

@Data
public class RulingResultKafkaMessage {
    private Long total;
    private String name;
    private Long id;
    private Long positiveVote;
    private VoteResultType result;
    private Long negativeVote;
}
