package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionControllerIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void canCreatePersonAndGetAPerson() {
       // setup
        Person alan = new Person(0, "Alan", "Software Engineer", "Football");

        ResponseEntity<Person> postResponse = restTemplate.postForEntity("http://localhost:" + port + "/person", alan, Person.class);

        assertThat(postResponse.getBody().getName()).isEqualTo(alan.getName());
        assertThat(postResponse.getBody().getRole()).isEqualTo(alan.getRole());
        assertThat(postResponse.getBody().getInterests()).isEqualTo(alan.getInterests());


        //action
        ResponseEntity<Person> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/person/" + postResponse.getBody().getId(), Person.class);


        //check
        assertThat(getResponse.getBody().getName()).isEqualTo(alan.getName());
        assertThat(getResponse.getBody().getRole()).isEqualTo(alan.getRole());
        assertThat(getResponse.getBody().getInterests()).isEqualTo(alan.getInterests());


    }

    @Test
    void canFindPeopleByTheirRole() {
        // setup - add people to db with roles
        Person alan = new Person(0, "Alan", "Software Engineer", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/person", alan, Person.class);
        Person gagan = new Person(0, "Gagan", "Software Engineer", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/person", gagan, Person.class);
        Person sam = new Person(0, "Sam", "Product Manager", "Football");
        restTemplate.postForEntity("http://localhost:" + port + "/person", sam, Person.class);

        //action - finding them based on role
        ResponseEntity<PersonList> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/person?role=\"Software Engineer\"", PersonList.class);


        //check - does it match what we set up
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Person> people = getResponse.getBody().getPeople();
        assertThat(people.size()).isEqualTo(2);
        assertThat(people).flatExtracting(Person::getName).containsExactlyInAnyOrder(alan.getName(), gagan.getName());
        assertThat(people).flatExtracting(Person::getRole).containsExactlyInAnyOrder(alan.getRole(), gagan.getRole());
    }
}
