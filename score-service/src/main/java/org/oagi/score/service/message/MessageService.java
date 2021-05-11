package org.oagi.score.service.message;

import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.message.MessageWriteRepository;
import org.oagi.score.repo.api.message.model.SendMessageRequest;
import org.oagi.score.repo.api.message.model.SendMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
public class MessageService {

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Async
    @Transactional
    public CompletableFuture<SendMessageResponse> asyncSendMessage(SendMessageRequest request) {
        MessageWriteRepository messageWriteRepository = scoreRepositoryFactory.createMessageWriteRepository();
        return CompletableFuture.supplyAsync(() -> messageWriteRepository.sendMessage(request));
    }
}
