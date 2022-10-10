package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionControllerIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setup() {
        personRepository.deleteAll();
    }

    @Test
    void canCreatePersonAndGetAPerson() {
       // setup
        Person alan = new Person(0, "Alan", "Software Engineer", "Football");

        ResponseEntity<Person> postResponse = restTemplate.postForEntity("http://localhost:" + port + "/people", alan, Person.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Person responseBody = postResponse.getBody();
        assertThat(responseBody.getName()).isEqualTo(alan.getName());
        assertThat(responseBody.getRole()).isEqualTo(alan.getRole());
        assertThat(responseBody.getInterests()).isEqualTo(alan.getInterests());

        //action
        ResponseEntity<Person> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/people/" + responseBody.getId(), Person.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //check
        responseBody = getResponse.getBody();
        assertThat(responseBody.getName()).isEqualTo(alan.getName());
        assertThat(responseBody.getRole()).isEqualTo(alan.getRole());
        assertThat(responseBody.getInterests()).isEqualTo(alan.getInterests());
    }

    @Test
    void canFindAllPeople() {
        // setup - add people to db with roles
        Person alan = new Person(0, "Alan", "Software Engineer", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", alan, Person.class);
        Person gagan = new Person(0, "Gagan", "Software Engineer", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", gagan, Person.class);
        Person sam = new Person(0, "Sam", "Product Manager", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", sam, Person.class);

        //action - finding them based on role
        ResponseEntity<PersonList> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/people", PersonList.class);

        //check - does it match what we set up
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Person> people = getResponse.getBody().getPeople();
        assertThat(people.size()).isEqualTo(3);
        assertThat(people).flatExtracting(Person::getName).containsExactlyInAnyOrder(alan.getName(), gagan.getName(), sam.getName());
    }

    @Test
    void canFindPeopleByTheirRole() {
        // setup - add people to db with roles
        String softwareEngineer = "Software Engineer";
        Person alan = new Person(0, "Alan", softwareEngineer, "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", alan, Person.class);
        Person gagan = new Person(0, "Gagan", softwareEngineer, "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", gagan, Person.class);
        Person sam = new Person(0, "Sam", "Product Manager", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", sam, Person.class);

        //action - finding them based on role
        ResponseEntity<PersonList> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/people?role=Software+Engineer", PersonList.class);

        //check - does it match what we set up
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Person> people = getResponse.getBody().getPeople();
        assertThat(people.size()).isEqualTo(2);
        assertThat(people).flatExtracting(Person::getName).containsExactlyInAnyOrder(alan.getName(), gagan.getName());
        assertThat(people).flatExtracting(Person::getRole).containsExactlyInAnyOrder(softwareEngineer, softwareEngineer);
    }

    @Test
    void canFindPeopleByTheirInterests() {
        // setup - add people to db with roles
        Person alan = new Person(0, "Alan", "Software Engineer", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", alan, Person.class);
        Person gagan = new Person(0, "Gagan", "Software Engineer", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", gagan, Person.class);
        Person sam = new Person(0, "Sam", "Product Manager", "Books");
        restTemplate.postForEntity("http://localhost:" + port + "/people", sam, Person.class);

        //action - finding them based on role
        ResponseEntity<PersonList> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/people?interests=Football", PersonList.class);


        //check - does it match what we set up
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Person> people = getResponse.getBody().getPeople();
        assertThat(people.size()).isEqualTo(2);
        assertThat(people).flatExtracting(Person::getName).containsExactlyInAnyOrder(alan.getName(), gagan.getName());
        assertThat(people).flatExtracting(Person::getInterests).containsExactlyInAnyOrder(alan.getInterests(), gagan.getInterests());
    }

    @Test
    void canFindPeopleByTheirInterestsAndRole() {
        // setup - add people to db with roles
        Person alan = new Person(0, "Alan", "Software Engineer", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/people", alan, Person.class);
        Person gagan = new Person(0, "Gagan", "Software Engineer", "Books");
        restTemplate.postForEntity("http://localhost:" + port + "/people", gagan, Person.class);
        Person sam = new Person(0, "Sam", "Product Manager", "Books");
        restTemplate.postForEntity("http://localhost:" + port + "/people", sam, Person.class);

        //action - finding them based on role
        ResponseEntity<PersonList> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/people?interests=Books&role=Software+Engineer", PersonList.class);


        //check - does it match what we set up
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Person> people = getResponse.getBody().getPeople();
        assertThat(people.size()).isEqualTo(1);
        assertThat(people).flatExtracting(Person::getName).containsExactlyInAnyOrder(gagan.getName());
    }
}
