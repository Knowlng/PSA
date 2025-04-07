package org.film.dto;

public class RateCommentRequest {
    private Long filmId;
    private Long commentUserId;
    private Boolean commentRating;

    public Long getFilmId() {
        return filmId;
    }
    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public Long getCommentUserId() {
        return commentUserId;
    }
    public void setCommentUserId(Long commentUserId) {
        this.commentUserId = commentUserId;
    }

    public Boolean getCommentRating() {
        return commentRating;
    }
    public void setCommentRating(Boolean commentRating) {
        this.commentRating = commentRating;
    }
}
