package org.film.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

import org.film.dto.GenreRequest;
import org.film.model.Genre;
import org.film.repository.GenreRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        Genre genre = new Genre();
        genre.setGenreName(genreRequest.getGenreName());
        try {
            Genre savedGenre = genreRepository.save(genre);
            return ResponseEntity.ok(savedGenre);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entry already exists");
        }
    }



    @GetMapping("/public/search-genre")
    public ResponseEntity<List<Map<String, Object>>> searchGenre(@RequestParam("query") String query) {
        List<Genre> genres = genreRepository.findByGenreNameContainingIgnoreCase(query);
        List<Map<String, Object>> results = genres.stream().map(genre -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", genre.getGenre_id());
            map.put("name", genre.getGenreName());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }


    @PutMapping("/admin/update-genre/{id}")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreRequest genreRequest) {
        
        Optional<Genre> existing = genreRepository.findByGenreName(genreRequest.getGenreName());
        if(existing.isPresent() && !existing.get().getGenre_id().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entry already exists");
        }
        
        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (!genreOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
        Genre genre = genreOptional.get();
        
        genre.setGenreName(genreRequest.getGenreName());
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
