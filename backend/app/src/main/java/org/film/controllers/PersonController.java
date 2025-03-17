package org.film.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.film.dto.PersonRequest;
import org.film.model.Person;
import org.film.repository.PersonRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostMapping("/create-person")
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonRequest personRequest) {

        Person person = new Person();
        person.setPersonFullName(personRequest.getPersonFullName());
        try {
            Person savedPerson = personRepository.save(person);
            return ResponseEntity.ok(savedPerson);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name already exists");
        }
    }

    @GetMapping("/search-person")
    public ResponseEntity<List<Map<String, Object>>> searchPerson(@RequestParam("query") String query) {
        List<Person> persons = personRepository.findByPersonFullNameContainingIgnoreCase(query);
        List<Map<String, Object>> results = persons.stream().map(person -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", person.getPerson_id());
            map.put("name", person.getPersonFullName());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @PutMapping("/update-person/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @Valid @RequestBody PersonRequest personRequest) {
        Optional<Person> existing = personRepository.findByPersonFullName(personRequest.getPersonFullName());
        if(existing.isPresent() && !existing.get().getPerson_id().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name already exists");
        }
        
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
        Person person = personOptional.get();
        
        person.setPersonFullName(personRequest.getPersonFullName());
        Person updatedPerson = personRepository.save(person);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/delete-person/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
        Person person = personOptional.get();
        personRepository.delete(person);
        return ResponseEntity.ok("Deleted successfully");
    }
}
