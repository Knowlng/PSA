package org.film.dto;

import java.time.LocalDate;
import java.util.List;

public class FilmFilterRequest {
    private String movieName;
    private List<String> selectedAgeRatings;
    private LocalDate fromDate;
    private LocalDate toDate;     
    private Long minGross;
    private Long maxGross;
    private List<ActorFilter> actorFilters;
    private List<Long> genreIds;
    private int page = 0;
    private int size = 10;
    private Double minRating;
    private Double maxRating;

    public String getMovieName() {
        return movieName;
    }
    
    public List<String> getSelectedAgeRatings() {
        return selectedAgeRatings;
    }

    public void setSelectedAgeRatings(List<String> selectedAgeRatings) {
        this.selectedAgeRatings = selectedAgeRatings;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Long getMinGross() {
        return minGross;
    }

    public void setMinGross(Long minGross) {
        this.minGross = minGross;
    }

    public Long getMaxGross() {
        return maxGross;
    }

    public void setMaxGross(Long maxGross) {
        this.maxGross = maxGross;
    }

    public List<ActorFilter> getActorFilters() {
        return actorFilters;
    }

    public void setActorFilters(List<ActorFilter> actorFilters) {
        this.actorFilters = actorFilters;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Double getMinRating() {
        return minRating;
    }

    public void setMinRating(Double minRating) {
        this.minRating = minRating;
    }

    public Double getMaxRating() {
        return maxRating;
    }
    
    public void setMaxRating(Double maxRating) {
        this.maxRating = maxRating;
    }
}
