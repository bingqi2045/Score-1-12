package org.oagi.srt.gateway.http.api.comment.service;

import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.cc_management.data.CcEvent;
import org.oagi.srt.gateway.http.api.comment.data.Comment;
import org.oagi.srt.gateway.http.api.comment.data.GetCommentRequest;
import org.oagi.srt.gateway.http.api.comment.data.PostCommentRequest;
import org.oagi.srt.gateway.http.api.comment.data.UpdateCommentRequest;
import org.oagi.srt.gateway.http.api.comment.repository.CommentRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public List<Comment> getComments(User user, GetCommentRequest request) {
        List<Comment> comments = repository.getCommentsByReference(request.getReference());
        return comments;
    }

    @Transactional
    public void postComments(User user, PostCommentRequest request) {
        long userId = sessionService.userId(user);

        long commentId = repository.insertComment()
                .setReference(request.getReference())
                .setText(request.getText())
                .setPrevCommentId(request.getPrevCommentId())
                .setCreatedBy(userId)
                .execute();

        Comment comment = repository.getCommentByCommentId(commentId);

        CcEvent event = new CcEvent();
        event.setAction("AddComment");
        event.addProperty("actor", user.getUsername());
        event.addProperty("text", comment.getText());
        event.addProperty("prevCommentId", comment.getPrevCommentId());
        event.addProperty("commentId", commentId);
        event.addProperty("timestamp", comment.getTimestamp());
        simpMessagingTemplate.convertAndSend("/topic/acc/" + request.getReference().replace("acc", ""), event);
    }

    @Transactional
    public void updateComments(User user, UpdateCommentRequest request) {
        long userId = sessionService.userId(user);
        Long ownerId = repository.getOwnerIdByCommentId(request.getCommentId());
        if (ownerId == null) {
            throw new EmptyResultDataAccessException(1);
        }

        if (ownerId != userId) {
            throw new DataAccessForbiddenException("Only allowed to modify the comment by the owner.");
        }

        CommentRepository.UpdateCommentArguments updateCommentArguments = repository.updateComment(userId);
        updateCommentArguments.setCommentId(request.getCommentId());
        if (request.getText() != null) {
            updateCommentArguments.setText(request.getText());
        }

        if (request.getDelete() != null) {
            if (repository.getCommentsByPrevCommentId(request.getCommentId()).size() > 0) {
                updateCommentArguments.setHide(request.getDelete());
            } else {
                updateCommentArguments.setDelete(request.getDelete());
            }
        }
        updateCommentArguments.execute();
    }
}
