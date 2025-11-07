package com.ticketsystem.ticketsystem.exception;


public class DuplicateUsernameException extends RuntimeException{
    public DuplicateUsernameException(String mssg){
        super(mssg);
    }

    public DuplicateUsernameException(String mssg , Throwable cause){
        super(mssg,cause);
    }
}
