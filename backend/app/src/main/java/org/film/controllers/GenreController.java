package org.film.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.film.dto.GenreRequest;
import org.film.model.Genre;
import org.film.repository.GenreRepository;
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

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class GenreController {

    private final GenreRepository genreRepository;

    public GenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @PostMapping("/admin/create-genre")
    public ResponseEntity<?> createGenre(@Valid @RequestBody GenreRequest genreRequest) {
        Optional<Genre> duplicate = genreRepository.findByGenreNameTranslations_En(genreRequest.getEnGenreName());
        if (duplicate.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("English entry already exists");
        }
        Genre genre = new Genre();

        Map<String, String> translations = new HashMap<>();
        translations.put("en", genreRequest.getEnGenreName());
        translations.put("lt", genreRequest.getLtGenreName());
        genre.setGenreNameTranslations(translations);

        try {
            Genre savedGenre = genreRepository.save(genre);
            return ResponseEntity.ok(savedGenre);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entry already exists");
        }
    }

    @GetMapping("/public/search-genre")
    public ResponseEntity<List<Map<String, Object>>> searchGenre(
            @RequestParam("query") String query,
            @RequestParam(value = "locale", defaultValue = "en") String locale) {
    
        List<Genre> genres;
        if ("lt".equalsIgnoreCase(locale)) {
            genres = genreRepository.findByGenreNameTranslations_LtContainingIgnoreCase(query);
        } else {
            genres = genreRepository.findByGenreNameTranslations_EnContainingIgnoreCase(query);
        }
    
        List<Map<String, Object>> results = genres.stream().map(genre -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", genre.getGenre_id());
            map.put("name", genre.getGenreNameTranslations().get(locale.toLowerCase()));
            return map;
        }).collect(Collectors.toList());
    
        return ResponseEntity.ok(results);
    }

    @GetMapping("/admin/genre/{id}/translations")
    public ResponseEntity<?> getGenreTranslations(@PathVariable Long id) {
        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (!genreOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        Map<String, String> translations = genreOptional.get().getGenreNameTranslations();
        return ResponseEntity.ok(translations);
    }
    

    @PutMapping("/admin/update-genre/{id}")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreRequest genreRequest) {

        Optional<Genre> existing = genreRepository.findByGenreNameTranslations_En(genreRequest.getEnGenreName());
        if (existing.isPresent() && !existing.get().getGenre_id().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entry already exists");
        }

        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (!genreOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
        Genre genre = genreOptional.get();

        Map<String, String> translations = new HashMap<>();
        translations.put("en", genreRequest.getEnGenreName());
        translations.put("lt", genreRequest.getLtGenreName());
        genre.setGenreNameTranslations(translations);

        Genre updatedGenre = genreRepository.save(genre);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/admin/delete-genre/{id}")
    @Transactional
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (genreOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }

        Genre genre = genreOptional.get();

        genre.getFilms().forEach(film -> film.getGenres().remove(genre));
        genre.getFilms().clear();
        genreRepository.delete(genre);

        return ResponseEntity.ok("Deleted successfully");
    }

}
