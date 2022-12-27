package com.desafio.desafiobackvotos.common.exceptions;

public class RulingNotFoundException extends RuntimeException{

    public RulingNotFoundException(final Long id) {
        super(String.format("Pauta %s não encontrada", id));
    }
}
