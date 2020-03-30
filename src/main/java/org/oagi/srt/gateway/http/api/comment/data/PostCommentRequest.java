package org.oagi.srt.gateway.http.api.comment.data;

import lombok.Data;

@Data
public class PostCommentRequest {

    private String reference;
    private String text;
    private Long prevCommentId;

}
