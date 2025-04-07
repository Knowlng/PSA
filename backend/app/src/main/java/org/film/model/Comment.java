package org.film.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "p_comment")
@Data
public class Comment {
    @EmbeddedId
    private CommentId id;
    
    @Column(name = "comment_text")
    @Size(max = 1000)
    private String commentText;
}
