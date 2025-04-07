package org.film.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.Objects;

@Embeddable
@Data
public class UserFilmCommentId implements Serializable {
    
    @Column(name = "liked_user_id")
    private Integer likedUserId;
    
    @Column(name = "comment_user_id")
    private Integer commentUserId;
    
    @Column(name = "comment_film_id")
    private Integer commentFilmId;

    public UserFilmCommentId() {}
    
    public UserFilmCommentId(Integer likedUserId, Integer commentUserId, Integer commentFilmId) {
        this.likedUserId = likedUserId;
        this.commentUserId = commentUserId;
        this.commentFilmId = commentFilmId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFilmCommentId)) return false;
        UserFilmCommentId that = (UserFilmCommentId) o;
        return Objects.equals(getLikedUserId(), that.getLikedUserId()) &&
               Objects.equals(getCommentUserId(), that.getCommentUserId()) &&
               Objects.equals(getCommentFilmId(), that.getCommentFilmId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLikedUserId(), getCommentUserId(), getCommentFilmId());
    }
}
