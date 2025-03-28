package org.film.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.film.dto.ActorFilter;
import org.film.dto.FilmFilterRequest;
import org.film.dto.FilmRequest;
import org.film.model.Film;
import org.film.model.FilmPerson;
import org.film.model.FilmPersonId;
import org.film.model.Genre;
import org.film.model.Person;
import org.film.repository.FilmRepository;
import org.film.repository.GenreRepository;
import org.film.repository.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class FilmController {

    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final PersonRepository personRepository;

    public FilmController(FilmRepository filmRepository, GenreRepository genreRepository, PersonRepository personRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.personRepository = personRepository;
    }

    @PostMapping("/admin/create-film")
    public ResponseEntity<?> createFilm(@Valid @RequestBody FilmRequest filmRequest) {

        if (filmRepository.findByFilmName(filmRequest.getFilmName().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Name already exists");
        }

        Film film = new Film();
        film.setFilmName(filmRequest.getFilmName().trim());
        film.setFilmDesc(filmRequest.getFilmDesc().trim());
        film.setFilmReleaseDate(filmRequest.getFilmReleaseDate());
        film.setFilmRating(filmRequest.getFilmRating());
        film.setFilmGross(filmRequest.getFilmGross());

        if (filmRequest.getGenreIds() != null && !filmRequest.getGenreIds().isEmpty()) {
            Set<Genre> genres = new HashSet<>();
            for (Long id : filmRequest.getGenreIds()) {
                Genre genre = genreRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Genre not found with id " + id));
                genres.add(genre);
            }
            film.setGenres(genres);
        }

        if (filmRequest.getPersons() != null && !filmRequest.getPersons().isEmpty()) {
            for (var fpReq : filmRequest.getPersons()) {
                Person person = personRepository.findById(fpReq.getPersonId())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Person not found with id " + fpReq.getPersonId()));

                FilmPerson filmPerson = new FilmPerson();
                filmPerson.setFilm(film);
                filmPerson.setPerson(person);
                filmPerson.setRole(fpReq.getRole());

                film.getFilmPersons().add(filmPerson);
            }
        }

        Film savedFilm = filmRepository.save(film);
        return ResponseEntity.ok(savedFilm);
    }

    @PutMapping("/admin/update-film/{id}")
    public ResponseEntity<?> updateFilm(@PathVariable Long id, @Valid @RequestBody FilmRequest filmRequest) {

        Film film = filmRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found"));

        Optional<Film> duplicateFilm = filmRepository.findByFilmName(filmRequest.getFilmName().trim());
        if (duplicateFilm.isPresent() && !duplicateFilm.get().getFilmId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Name already exists");
        }

        film.setFilmName(filmRequest.getFilmName().trim());
        film.setFilmDesc(filmRequest.getFilmDesc().trim());
        film.setFilmReleaseDate(filmRequest.getFilmReleaseDate());
        film.setFilmRating(filmRequest.getFilmRating());
        film.setFilmGross(filmRequest.getFilmGross());

        film.getGenres().clear();
        if (filmRequest.getGenreIds() != null && !filmRequest.getGenreIds().isEmpty()) {
            for (Long genreId : filmRequest.getGenreIds()) {
                Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Genre not found with id " + genreId));
                film.getGenres().add(genre);
            }
        }

        film.getFilmPersons().clear();
        if (filmRequest.getPersons() != null && !filmRequest.getPersons().isEmpty()) {
            Set<Long> processedPersonIds = new HashSet<>();
            for (var fpReq : filmRequest.getPersons()) {
                if (!processedPersonIds.add(fpReq.getPersonId())) {
                    continue;
                }
                Person person = personRepository.findById(fpReq.getPersonId())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Person not found with id " + fpReq.getPersonId()));

                FilmPerson filmPerson = new FilmPerson();
                FilmPersonId filmPersonId = new FilmPersonId();
                filmPersonId.setFilmId(film.getFilmId());
                filmPersonId.setPersonId(person.getPerson_id());

                filmPerson.setId(filmPersonId);
                filmPerson.setFilm(film);
                filmPerson.setPerson(person);
                filmPerson.setRole(fpReq.getRole());

                film.getFilmPersons().add(filmPerson);
            }
        }

        Film updatedFilm = filmRepository.save(film);
        return ResponseEntity.ok(updatedFilm);
    }



    @GetMapping("/public/search-film")
    public ResponseEntity<List<Map<String, Object>>> searchFilm(@RequestParam("query") String query) {
        List<Film> films = filmRepository.findByFilmNameContainingIgnoreCase(query);
        List<Map<String, Object>> results = films.stream().map(film -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", film.getFilmId());
            map.put("name", film.getFilmName());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/public/film/{id}")
    public ResponseEntity<?> getFilmDetails(@PathVariable Long id) {
        Optional<Film> filmOptional = filmRepository.findById(id);

        if (filmOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
        }

        Film film = filmOptional.get();

        Map<String, Object> result = new HashMap<>();

        result.put("id", film.getFilmId());
        result.put("filmName", film.getFilmName());
        result.put("filmDesc", film.getFilmDesc());
        result.put("filmReleaseDate", film.getFilmReleaseDate());
        result.put("filmRating", film.getFilmRating());
        result.put("filmGross", film.getFilmGross());

        Set<Map<String, Object>> actors = film.getFilmPersons().stream().map(fp -> {
            Map<String, Object> actorMap = new HashMap<>();
            actorMap.put("id", fp.getPerson().getPerson_id());
            actorMap.put("name", fp.getPerson().getPersonFullName());
            actorMap.put("role", fp.getRole());
            return actorMap;
        }).collect(Collectors.toSet());

        Set<Map<String, Object>> genres = film.getGenres().stream()
            .map(genre -> {
                Map<String, Object> genreMap = new HashMap<>();
                genreMap.put("id", genre.getGenre_id());
                genreMap.put("name", genre.getGenreName());
                return genreMap;
            })
            .collect(Collectors.toSet());

        result.put("actors", actors);
        result.put("genres", genres);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/admin/delete-film/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable Long id) {
        Optional<Film> filmOptional = filmRepository.findById(id);
        if (filmOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
        }

        filmRepository.delete(filmOptional.get());
        return ResponseEntity.ok("Film deleted successfully");
    }

    @SuppressWarnings("unused")
    @PostMapping("/public/films/filter")
    public ResponseEntity<Page<Map<String, Object>>> filterFilms(@RequestBody FilmFilterRequest filterRequest) {
        int pageIndex = Math.max(filterRequest.getPage() - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, filterRequest.getSize());
        Specification<Film> spec = Specification.where(null);

        if (filterRequest.getMovieName() != null && !filterRequest.getMovieName().trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("filmName")), "%" + filterRequest.getMovieName().trim().toLowerCase() + "%")
            );
        }
        if (filterRequest.getSelectedAgeRatings() != null && !filterRequest.getSelectedAgeRatings().isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("filmRating").in(filterRequest.getSelectedAgeRatings()));
        }
        if (filterRequest.getFromDate() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("filmReleaseDate"), filterRequest.getFromDate()));
        }
        if (filterRequest.getToDate() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("filmReleaseDate"), filterRequest.getToDate()));
        }
        if (filterRequest.getMinGross() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("filmGross"), filterRequest.getMinGross()));
        }
        if (filterRequest.getMaxGross() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("filmGross"), filterRequest.getMaxGross()));
        }
        if (filterRequest.getActorFilters() != null && !filterRequest.getActorFilters().isEmpty()) {
            for (ActorFilter af : filterRequest.getActorFilters()) {
                spec = spec.and((root, query, cb) -> {
                    Join<Film, FilmPerson> fpJoin = root.join("filmPersons", JoinType.INNER);
                    Predicate actorPredicate = cb.equal(fpJoin.get("person").get("person_id"), af.getActorId());
                    if (af.getRole() != null && !af.getRole().equalsIgnoreCase("any role")) {
                        actorPredicate = cb.and(actorPredicate, cb.equal(fpJoin.get("role"), af.getRole()));
                    }
                    return actorPredicate;
                });
            }
        }
        if (filterRequest.getGenreIds() != null && !filterRequest.getGenreIds().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Join<Film, Genre> genreJoin = root.join("genres", JoinType.INNER);
                return genreJoin.get("genre_id").in(filterRequest.getGenreIds());
            });
        }

        Page<Film> filmPage = filmRepository.findAll(spec, pageable);

        Page<Map<String, Object>> resultPage = filmPage.map(film -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", film.getFilmId());
            result.put("filmName", film.getFilmName());
            result.put("filmDesc", film.getFilmDesc());
            result.put("filmReleaseDate", film.getFilmReleaseDate());
            result.put("filmRating", film.getFilmRating());
            result.put("filmGross", film.getFilmGross());

            Set<Map<String, Object>> actors = film.getFilmPersons().stream().map(fp -> {
                Map<String, Object> actorMap = new HashMap<>();
                actorMap.put("id", fp.getPerson().getPerson_id());
                actorMap.put("name", fp.getPerson().getPersonFullName());
                actorMap.put("role", fp.getRole());
                return actorMap;
            }).collect(Collectors.toSet());

            Set<Map<String, Object>> genres = film.getGenres().stream().map(genre -> {
                Map<String, Object> genreMap = new HashMap<>();
                genreMap.put("id", genre.getGenre_id());
                genreMap.put("name", genre.getGenreName());
                return genreMap;
            }).collect(Collectors.toSet());

            result.put("actors", actors);
            result.put("genres", genres);
            return result;
        });

        return ResponseEntity.ok(resultPage);
    }


}
