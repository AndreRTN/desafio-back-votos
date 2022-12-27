package com.desafio.desafiobackvotos.common.exceptions;

public class AssociateAlreadyVotedException extends RuntimeException{
    public AssociateAlreadyVotedException(final String msg) {
        super(String.format("Esse cpf Já votou na Pauta: %s", msg));
    }

}
