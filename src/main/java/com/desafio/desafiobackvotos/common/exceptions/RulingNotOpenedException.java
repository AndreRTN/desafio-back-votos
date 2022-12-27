package com.desafio.desafiobackvotos.common.exceptions;

public class RulingNotOpenedException extends RuntimeException{
    public RulingNotOpenedException() {
        super("Não é possível votar em uma Pauta que ainda não foi aberta para votação");
    }
    public RulingNotOpenedException(final String msg) {
        super(msg);
    }
}
