package com.desafio.desafiobackvotos.common.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRestResult {
    private Integer status;
    private String message;
}
