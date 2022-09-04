package org.oagi.score.gateway.http.api.comment.data;

import lombok.Data;

@Data
public class UpdateCommentRequest {

    private String commentId;

    private String text;
    private Boolean hide;
    private Boolean delete;

}
