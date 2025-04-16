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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @PostMapping("/admin/create-person")
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonRequest personRequest) {
        Optional<Person> duplicate = personRepository.findByPersonNameTranslations_En(personRequest.getEnPersonName());
        if (duplicate.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("English entry already exists");
        }

        Person person = new Person();
        Map<String, String> translations = new HashMap<>();
        translations.put("en", personRequest.getEnPersonName());
        translations.put("lt", personRequest.getLtPersonName());
        person.setPersonNameTranslations(translations);

        try {
            Person savedPerson = personRepository.save(person);
            return ResponseEntity.ok(savedPerson);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entry already exists");
        }
    }

    @GetMapping("/public/search-person")
    public ResponseEntity<List<Map<String, Object>>> searchPerson(
            @RequestParam("query") String query,
            @RequestParam(value = "locale", defaultValue = "en") String locale) {

        List<Person> persons;
        if ("lt".equalsIgnoreCase(locale)) {
            persons = personRepository.findByPersonNameTranslations_LtContainingIgnoreCase(query);
        } else {
            persons = personRepository.findByPersonNameTranslations_EnContainingIgnoreCase(query);
        }

        List<Map<String, Object>> results = persons.stream().map(person -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", person.getPerson_id());
            String name = person.getPersonNameTranslations().get(locale.toLowerCase());
            if (name == null) {
                name = person.getPersonNameTranslations().get("en");
            }
            map.put("name", name);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    @PutMapping("/admin/update-person/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @Valid @RequestBody PersonRequest personRequest) {
        Optional<Person> existing = personRepository.findByPersonNameTranslations_En(personRequest.getEnPersonName());
        if (existing.isPresent() && !existing.get().getPerson_id().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entry already exists");
        }
        
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
        
        Person person = personOptional.get();
        Map<String, String> translations = new HashMap<>();
        translations.put("en", personRequest.getEnPersonName());
        translations.put("lt", personRequest.getLtPersonName());
        person.setPersonNameTranslations(translations);
        
        Person updatedPerson = personRepository.save(person);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/admin/delete-person/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
        Person person = personOptional.get();
        personRepository.delete(person);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/admin/person/{id}/translations")
    public ResponseEntity<?> getGenreTranslations(@PathVariable Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        Map<String, String> translations = personOptional.get().getPersonNameTranslations();
        return ResponseEntity.ok(translations);
    }
    
}
