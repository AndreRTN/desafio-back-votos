package com.desafio.desafiobackvotos.common.pojo;


import lombok.Data;

@Data
public class RulingResultKafkaMessage {
    private Long total;
    private String name;
    private Long id;
    private Long positiveVote;
    private Long negativeVote;
}
