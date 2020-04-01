package org.oagi.srt.gateway.http.api.comment.service;

import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.comment.data.Comment;
import org.oagi.srt.gateway.http.api.comment.data.GetCommentRequest;
import org.oagi.srt.gateway.http.api.comment.data.PostCommentRequest;
import org.oagi.srt.gateway.http.api.comment.data.UpdateCommentRequest;
import org.oagi.srt.gateway.http.api.comment.repository.CommentRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private SessionService sessionService;

    public List<Comment> getComments(User user, GetCommentRequest request) {
        List<Comment> comments = repository.getCommentsByReference(request.getReference());
        return comments;
    }

    public List<Comment> getComments(User user, long commentId) {
        List<Comment> comments = repository.getCommentsByCommentId(commentId);
        return comments;
    }

    @Transactional
    public void postComments(User user, PostCommentRequest request) {
        long userId = sessionService.userId(user);

        repository.insertComment()
                .setReference(request.getReference())
                .setText(request.getText())
                .setPrevCommentId(request.getPrevCommentId())
                .setCreatedBy(userId)
                .execute();
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

        if (request.getHide() != null) {
            updateCommentArguments.setHide(request.getHide());
        }

        if (request.getDelete() != null) {
            updateCommentArguments.setDelete(request.getDelete());
        }
        updateCommentArguments.execute();
    }
}
