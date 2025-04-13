package org.film.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.film.dto.ActorFilter;
import org.film.dto.CommentFilterRequest;
import org.film.dto.CommentRequest;
import org.film.dto.FilmFilterRequest;
import org.film.dto.FilmRequest;
import org.film.dto.RateCommentRequest;
import org.film.dto.RateFilmRequest;
import org.film.model.Comment;
import org.film.model.CommentId;
import org.film.model.Film;
import org.film.model.FilmPerson;
import org.film.model.FilmPersonId;
import org.film.model.Genre;
import org.film.model.Person;
import org.film.model.User;
import org.film.model.UserFilm;
import org.film.model.UserFilmComment;
import org.film.model.UserFilmCommentId;
import org.film.model.UserFilmId;
import org.film.repository.CommentRepository;
import org.film.repository.FilmRepository;
import org.film.repository.GenreRepository;
import org.film.repository.PersonRepository;
import org.film.repository.UserFilmCommentRepository;
import org.film.repository.UserFilmRepository;
import org.film.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private final UserRepository userRepository;
    private final UserFilmRepository userFilmRepository;
    private final CommentRepository commentRepository;
    private final UserFilmCommentRepository userFilmCommentRepository;

    public FilmController(FilmRepository filmRepository, 
                          GenreRepository genreRepository, 
                          PersonRepository personRepository,
                          UserRepository userRepository,
                          UserFilmRepository userFilmRepository,
                          CommentRepository commentRepository,
                          UserFilmCommentRepository userFilmCommentRepository) {
        this.commentRepository = commentRepository;
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.userFilmRepository = userFilmRepository;
        this.userFilmCommentRepository = userFilmCommentRepository;
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
            film.getFilmPersons().clear();
            for (var fpReq : filmRequest.getPersons()) {
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
    public ResponseEntity<Page<Map<String, Object>>> filterFilms(
            @RequestBody FilmFilterRequest filterRequest, Authentication auth) {
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
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("filmReleaseDate"), filterRequest.getFromDate())
            );
        }
        if (filterRequest.getToDate() != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("filmReleaseDate"), filterRequest.getToDate())
            );
        }
        if (filterRequest.getMinGross() != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("filmGross"), filterRequest.getMinGross())
            );
        }
        if (filterRequest.getMaxGross() != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("filmGross"), filterRequest.getMaxGross())
            );
        }
        if (filterRequest.getActorFilters() != null && !filterRequest.getActorFilters().isEmpty()) {
            for (ActorFilter af : filterRequest.getActorFilters()) {
                spec = spec.and((root, query, cb) -> {
                    Join<Film, FilmPerson> fpJoin = root.join("filmPersons", JoinType.INNER);
                    Predicate actorPredicate = cb.equal(fpJoin.get("person").get("person_id"), af.getActorId());
                    if (af.getRole() != null && !af.getRole().equalsIgnoreCase("any role")) {
                        actorPredicate = cb.and(actorPredicate, cb.equal(fpJoin.get("id").get("role"), af.getRole()));
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

        if (filterRequest.getMinRating() != null) {
            spec = spec.and((root, query, cb) -> {
                jakarta.persistence.criteria.Subquery<Double> avgSubquery = query.subquery(Double.class);
                jakarta.persistence.criteria.Root<UserFilm> uf = avgSubquery.from(UserFilm.class);
                avgSubquery.select(cb.avg(uf.get("rating")));
                avgSubquery.where(cb.equal(uf.get("film").get("filmId"), root.get("filmId")));
                return cb.greaterThanOrEqualTo(avgSubquery, filterRequest.getMinRating());
            });
        }
        if (filterRequest.getMaxRating() != null) {
            spec = spec.and((root, query, cb) -> {
                jakarta.persistence.criteria.Subquery<Double> avgSubquery = query.subquery(Double.class);
                jakarta.persistence.criteria.Root<UserFilm> uf = avgSubquery.from(UserFilm.class);
                avgSubquery.select(cb.avg(uf.get("rating")));
                avgSubquery.where(cb.equal(uf.get("film").get("filmId"), root.get("filmId")));
                return cb.lessThanOrEqualTo(avgSubquery, filterRequest.getMaxRating());
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

            List<UserFilm> filmRatings = userFilmRepository.findByIdFilmId(film.getFilmId());
            if (!filmRatings.isEmpty()) {
                double average = filmRatings.stream().mapToInt(UserFilm::getRating).average().orElse(0.0);
                double roundedAverage = Math.round(average * 10.0) / 10.0;
                result.put("averageRating", roundedAverage);
            }

            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                Optional<User> userOpt = userRepository.findByUserName(auth.getName());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    UserFilmId userFilmId = new UserFilmId();
                    userFilmId.setUserId(user.getUserId());
                    userFilmId.setFilmId(film.getFilmId());
                    Optional<UserFilm> userFilmOpt = userFilmRepository.findById(userFilmId);
                    if (userFilmOpt.isPresent()) {
                        result.put("userRating", userFilmOpt.get().getRating());
                    }
                }
            }

            return result;
        });

        return ResponseEntity.ok(resultPage);
    }

    @PostMapping("/auth/rate-film")
    public ResponseEntity<?> rateFilm(@Valid @RequestBody RateFilmRequest rateRequest, Authentication auth) {
        if (rateRequest.getRating() < 1 || rateRequest.getRating() > 10) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 10");
        }
        
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        
        Optional<Film> filmOpt = filmRepository.findById(rateRequest.getFilmId());
        if (!filmOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
        }
        Film film = filmOpt.get();
        
        UserFilmId id = new UserFilmId();
        id.setUserId(user.getUserId());
        id.setFilmId(film.getFilmId());
        
        Optional<UserFilm> existingRating = userFilmRepository.findById(id);
        UserFilm uf;
        if (existingRating.isPresent()) {
            uf = existingRating.get();
            uf.setRating(rateRequest.getRating());
        } else {
            uf = new UserFilm();
            uf.setId(id);
            uf.setUser(user);
            uf.setFilm(film);
            uf.setRating(rateRequest.getRating());
        }
        
        userFilmRepository.save(uf);
        
        Map<String, Object> result = new HashMap<>();
        result.put("rating", uf.getRating());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/auth/film-rating")
    public ResponseEntity<?> getFilmRating(@RequestParam("filmId") Long filmId, Authentication auth) {
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        
        UserFilmId id = new UserFilmId();
        id.setUserId(user.getUserId());
        id.setFilmId(filmId);
        
        Optional<UserFilm> ratingOpt = userFilmRepository.findById(id);
        
        Map<String, Object> result = new HashMap<>();
        if (ratingOpt.isPresent()) {
            result.put("rating", ratingOpt.get().getRating());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/public/film/{filmId}/average-rating")
    public ResponseEntity<?> getAverageFilmRating(@PathVariable Long filmId) {
        List<UserFilm> ratings = userFilmRepository.findByIdFilmId(filmId);
        
        if (ratings.isEmpty()) {
            return ResponseEntity.ok(new HashMap<>());
        }
        
        double average = ratings.stream().mapToInt(UserFilm::getRating).average().orElse(0.0);
        double roundedAverage = Math.round(average * 10.0) / 10.0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", roundedAverage);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/auth/comment")
    public ResponseEntity<?> saveComment(@Valid @RequestBody CommentRequest commentRequest, Authentication auth) {
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        
        Optional<Film> filmOpt = filmRepository.findById(commentRequest.getFilmId());
        if (!filmOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
        }
        Film film = filmOpt.get();
        
        CommentId id = new CommentId();
        id.setUserId(user.getUserId());
        id.setFilmId(film.getFilmId());
        
        Optional<Comment> existingComment = commentRepository.findById(id);
        Comment comment;
        if (existingComment.isPresent()) {
            comment = existingComment.get();
            comment.setCommentText(commentRequest.getCommentText());
        } else {
            comment = new Comment();
            comment.setId(id);
            comment.setCommentText(commentRequest.getCommentText());
        }
        
        commentRepository.save(comment);
        return ResponseEntity.ok("Comment saved successfully");
    }

    @GetMapping("/auth/comment")
    public ResponseEntity<?> getComment(@RequestParam("filmId") Long filmId, Authentication auth) {
        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();
        
        CommentId id = new CommentId();
        id.setUserId(user.getUserId());
        id.setFilmId(filmId);
        
        Optional<Comment> commentOpt = commentRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (commentOpt.isPresent()) {
            result.put("commentText", commentOpt.get().getCommentText());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/public/film/comments")
    public ResponseEntity<Page<Map<String, Object>>> getCommentsForFilm(
            @RequestBody CommentFilterRequest commentFilterRequest,
            Authentication auth) {

        Long filmId = commentFilterRequest.getFilmId();
        int page = Math.max(commentFilterRequest.getPage() - 1, 0);
        int size = commentFilterRequest.getSize();
        
        List<Comment> allComments = commentRepository.findByIdFilmId(filmId);

        final Optional<User> currentUserOpt = (auth != null && auth.isAuthenticated() 
            && !auth.getName().equals("anonymousUser"))
                ? userRepository.findByUserName(auth.getName())
                : Optional.empty();

        List<Comment> filteredComments = allComments.stream()
                .filter(comment -> comment.getCommentText() != null && !comment.getCommentText().trim().isEmpty())
                .collect(Collectors.toList());

        List<Map<String, Object>> resultList = filteredComments.stream().map(comment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", comment.getId().getUserId());
            map.put("filmId", comment.getId().getFilmId());
            map.put("commentText", comment.getCommentText());
            Optional<User> commentUser = userRepository.findById(comment.getId().getUserId());
            map.put("userName", commentUser.map(User::getUserName).orElse("Unknown"));
            
            UserFilmId userFilmId = new UserFilmId();
            userFilmId.setUserId(comment.getId().getUserId());
            userFilmId.setFilmId(comment.getId().getFilmId());
            Optional<UserFilm> userFilmOpt = userFilmRepository.findById(userFilmId);
            map.put("userRating", userFilmOpt.map(UserFilm::getRating).orElse(null));

            List<UserFilmComment> commentRatings = userFilmCommentRepository
                    .findByIdCommentUserIdAndIdCommentFilmId(
                            comment.getId().getUserId().intValue(),
                            comment.getId().getFilmId().intValue());
            int totalCommentRating = commentRatings.stream()
                    .mapToInt(r -> r.getCommentRating() ? 1 : -1)
                    .sum();
            map.put("totalCommentRating", totalCommentRating);

            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                UserFilmCommentId ufCommentId = new UserFilmCommentId();
                ufCommentId.setLikedUserId(currentUser.getUserId().intValue());
                ufCommentId.setCommentUserId(comment.getId().getUserId().intValue());
                ufCommentId.setCommentFilmId(comment.getId().getFilmId().intValue());
                
                Optional<UserFilmComment> userCommentRatingOpt = userFilmCommentRepository.findById(ufCommentId);
                map.put("userCommentRating", userCommentRatingOpt.map(UserFilmComment::getCommentRating).orElse(null));
            } else {
                map.put("userCommentRating", null);
            }
            return map;
        }).collect(Collectors.toList());

        final Integer filterTypeId = (commentFilterRequest.getFilterTypeId() == null 
                ? 1 : commentFilterRequest.getFilterTypeId());
        final String sortOrder = (commentFilterRequest.getSortOrder() == null || commentFilterRequest.getSortOrder().isEmpty() 
                ? "DESC" : commentFilterRequest.getSortOrder());

        if (filterTypeId == 2 || filterTypeId == 3) {
            if (currentUserOpt.isPresent()) {
                resultList = resultList.stream().filter(map -> {
                    Object userCommentRating = map.get("userCommentRating");
                    if (filterTypeId == 2) {
                        return Boolean.TRUE.equals(userCommentRating);
                    } else {
                        return Boolean.FALSE.equals(userCommentRating);
                    }
                }).collect(Collectors.toList());
            } else {
                resultList = new ArrayList<>();
            }
        } else if (filterTypeId == 1) {
            Comparator<Map<String, Object>> comparator = Comparator.comparingInt(
                    map -> (Integer) map.get("totalCommentRating"));
            if ("DESC".equalsIgnoreCase(sortOrder)) {
                comparator = comparator.reversed();
            }
            resultList.sort(comparator);
        }

        int total = resultList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        List<Map<String, Object>> pagedResult = new ArrayList<>();
        if (start < end) {
            pagedResult = resultList.subList(start, end);
        }
        
        Page<Map<String, Object>> resultPage = new PageImpl<>(pagedResult, PageRequest.of(page, size), total);
        return ResponseEntity.ok(resultPage);
    }



    @DeleteMapping("/auth/comment")
    public ResponseEntity<?> deleteComment(@RequestParam("filmId") Long filmId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String username = auth.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOpt.get();

        CommentId commentId = new CommentId();
        commentId.setUserId(user.getUserId());
        commentId.setFilmId(filmId);

        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        }

        commentRepository.deleteById(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @PostMapping("/auth/rate-comment")
    public ResponseEntity<?> rateComment(@Valid @RequestBody RateCommentRequest request, Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        Optional<User> userOpt = userRepository.findByUserName(auth.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User likedUser = userOpt.get();

        Long filmId = request.getFilmId();
        Long commentUserId = request.getCommentUserId();
        Boolean commentRating = request.getCommentRating();

        if (!filmRepository.existsById(filmId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
        }

        CommentId commentId = new CommentId();
        commentId.setUserId(commentUserId);
        commentId.setFilmId(filmId);
        if (!commentRepository.existsById(commentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        }

        UserFilmCommentId ufCommentId = new UserFilmCommentId();
        ufCommentId.setLikedUserId(likedUser.getUserId().intValue());
        ufCommentId.setCommentUserId(commentUserId.intValue());
        ufCommentId.setCommentFilmId(filmId.intValue());

        if (commentRating == null) {
            if (userFilmCommentRepository.existsById(ufCommentId)) {
                userFilmCommentRepository.deleteById(ufCommentId);
                return ResponseEntity.ok("Comment rating removed successfully");
            } else {
                return ResponseEntity.ok("No rating entry found to remove");
            }
        } else {
            UserFilmComment userFilmComment = userFilmCommentRepository.findById(ufCommentId)
                    .orElseGet(UserFilmComment::new);
            userFilmComment.setId(ufCommentId);
            userFilmComment.setFilmId(filmId.intValue());
            userFilmComment.setCommentRating(commentRating);
            
            userFilmCommentRepository.save(userFilmComment);
            return ResponseEntity.ok("Comment rating saved successfully");
        }
    }

    @DeleteMapping("/admin/comment")
    public ResponseEntity<?> adminDeleteComment(
            @RequestParam("userId") Long userId,
            @RequestParam("filmId") Long filmId) {
        
        CommentId commentId = new CommentId();
        commentId.setUserId(userId);
        commentId.setFilmId(filmId);

        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        }

        commentRepository.deleteById(commentId);
        
        return ResponseEntity.ok("Comment deleted successfully");
    }

}
