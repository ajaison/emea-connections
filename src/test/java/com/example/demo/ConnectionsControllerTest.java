package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ConnectionsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello World")));
    }

    @Test
    public void shouldCreateAPerson() throws Exception {
        Person alan = new Person(1, "Alan", "Software Engineer", "Football");
        Person gagan = new Person(2, "Gagan", "Software Engineer", "Books");
        when(personRepository.save(any())).thenReturn(alan).thenReturn(gagan);


        this.mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"Alan\", \"role\": \"Software Engineer\", \"interests\": \"Football\"}")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1 ,\"name\": \"Alan\", \"role\": \"Software Engineer\", \"interests\": \"Football\"}"));

        this.mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"Gagan\", \"role\": \"Software Engineer\", \"interests\": \"Books\"}")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 2 ,\"name\": \"Gagan\", \"role\": \"Software Engineer\", \"interests\": \"Books\"}"));
    }

    @Test
    public void shouldFindAPersonOnId() throws Exception {
        Person alan = new Person(1, "Alan", "Software Engineer", "Football");
        when(personRepository.findById(1)).thenReturn(Optional.of(alan));

        this.mockMvc.perform(get("/person/1").contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1 ,\"name\": \"Alan\", \"role\": \"Software Engineer\", \"interests\": \"Football\"}"));

    }

    @Test
    public void shouldThrowANotFoundErrorForAnUnknownId() throws Exception {
        when(personRepository.findById(2)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/person/2").contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Can't find person with id: 2")));
    }
}
