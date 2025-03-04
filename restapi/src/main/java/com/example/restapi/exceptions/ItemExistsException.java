package com.example.restapi.exceptions;

public class ItemExistsException extends RuntimeException{
    public ItemExistsException(String message) {
        super(message);
    }
}
