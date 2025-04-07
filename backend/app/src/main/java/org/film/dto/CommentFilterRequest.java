package org.film.dto;

public class CommentFilterRequest {
    private Long filmId;
    private int page = 0;
    private int size = 10;

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
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
}
