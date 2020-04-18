package org.oagi.srt.gateway.http.api.comment.controller;

import org.jooq.tools.StringUtils;
import org.oagi.srt.gateway.http.api.comment.data.Comment;
import org.oagi.srt.gateway.http.api.comment.data.GetCommentRequest;
import org.oagi.srt.gateway.http.api.comment.data.PostCommentRequest;
import org.oagi.srt.gateway.http.api.comment.data.UpdateCommentRequest;
import org.oagi.srt.gateway.http.api.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentService service;

    @RequestMapping(value = "/comments/{reference}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Comment> getComments(@AuthenticationPrincipal User user,
                                     @PathVariable("reference") String reference) {
        if (StringUtils.isEmpty(reference)) {
            throw new IllegalArgumentException("'reference' parameter must not be empty.");
        }

        GetCommentRequest request = new GetCommentRequest();
        request.setReference(reference);

        return service.getComments(user, request);
    }

    @RequestMapping(value = "/comment/{reference}", method = RequestMethod.PUT)
    public void postComment(@AuthenticationPrincipal User user,
                            @PathVariable("reference") String reference,
                            @RequestBody PostCommentRequest request) {
        if (StringUtils.isEmpty(reference)) {
            throw new IllegalArgumentException("'reference' parameter must not be empty.");
        }

        request.setReference(reference);

        service.postComments(user, request);
    }

    @RequestMapping(value = "/comment/{commentId}", method = RequestMethod.POST)
    public void updateComment(@AuthenticationPrincipal User user,
                              @PathVariable("commentId") long commentId,
                              @RequestBody UpdateCommentRequest request) {
        request.setCommentId(commentId);

        service.updateComments(user, request);
    }
}
