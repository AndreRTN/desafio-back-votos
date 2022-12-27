package com.desafio.desafiobackvotos.common.exceptions;


import com.desafio.desafiobackvotos.common.type.VoteType;

public class UnableToVoteException extends RuntimeException{
    public UnableToVoteException(String msg) {
        super(String.format("CPF %s %s",msg, VoteType.UNABLE_TO_VOTE ));
    }
}
