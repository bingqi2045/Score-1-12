package org.oagi.score.gateway.http.api.comment.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {

    private String commentId;
    private String text;

    private String loginId;
    private LocalDateTime timestamp;

    private boolean hidden;
    private String prevCommentId;

}
