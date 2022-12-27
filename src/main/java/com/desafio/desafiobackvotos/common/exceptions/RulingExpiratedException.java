package com.desafio.desafiobackvotos.common.exceptions;

public class RulingExpiratedException extends RuntimeException{

    public RulingExpiratedException(final Long id) {
        super(String.format("A Pauta %s Já expirou", id));
    }
}
