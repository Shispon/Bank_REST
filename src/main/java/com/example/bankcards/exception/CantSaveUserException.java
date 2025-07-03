package com.example.bankcards.exception;



public class CantSaveUserException extends RuntimeException {
    public CantSaveUserException() {
        super("Ошибка при сохранении убедитесь что почта и userName уникальны");
    }
}
