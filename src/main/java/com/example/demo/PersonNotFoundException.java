package com.example.demo;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(Integer id) {
        super("Can't find person with id: " + id);
    }
}
