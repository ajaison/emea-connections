package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ConnectionsController {

    private final PersonRepository personRepository;

    @Autowired
    public ConnectionsController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostMapping("/people")
    @CrossOrigin(origins = "http://localhost:3000")
    @ResponseStatus(HttpStatus.CREATED)
    public Person createPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }


    @GetMapping("/people/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public Person getPerson(@PathVariable Integer id) {
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }


    @GetMapping("/people")
    @CrossOrigin(origins = "http://localhost:3000")
    public PersonList getPeople(@RequestParam Optional<String> role, @RequestParam Optional<String> interests) {
        Stream<Person> people = personRepository.findAll().stream();

        if (interests.isPresent()) {
            people = people.filter(person -> interests.get().equalsIgnoreCase(person.getInterests()));
        }

        if (role.isPresent()) {
            people = people.filter(person -> role.get().equalsIgnoreCase(person.getRole()));
        }

        PersonList list = new PersonList();
        list.setPeople(people.toList());
        return list;
    }
}
