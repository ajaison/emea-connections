package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ConnectionsController {

    private PersonRepository personRepository;

    public ConnectionsController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/person")
    public Person createPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/person/{id}")
    public Person getPerson(@PathVariable Integer id) {
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/person/all")
    public Person getPeople() {
        return (Person) personRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/person")
    public PersonList getPersonByRole(@RequestParam String role) {
        PersonList response = new PersonList();
        response.setPeople(personRepository.findPeopleByRoleEqualsIgnoreCase(role.replaceAll("^\"|\"$", "")));
        return response;
    }
//test
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/person/interests")
    public PersonList getPersonByInterests(@RequestParam String interests) {
        PersonList response = new PersonList();
        response.setPeople(personRepository.findPeopleByInterestsEqualsIgnoreCase(interests.replaceAll("^\"|\"$", "")));
        return response;
    }
}
