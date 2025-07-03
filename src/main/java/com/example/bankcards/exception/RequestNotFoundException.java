package com.example.bankcards.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException() {
        super("Такого запроса не найдено");
    }
}
