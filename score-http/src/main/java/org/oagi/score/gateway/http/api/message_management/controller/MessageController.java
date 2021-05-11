package org.oagi.score.gateway.http.api.message_management.controller;

import org.oagi.score.gateway.http.api.message_management.data.CountOfUnreadMessages;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.service.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/message/count_of_unread", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CountOfUnreadMessages countOfUnreadMessages(
            @AuthenticationPrincipal AuthenticatedPrincipal requester) {
        CountOfUnreadMessages countOfUnreadMessages = new CountOfUnreadMessages();
        if (requester == null) {
            countOfUnreadMessages.setCountOfUnreadMessages(-1);
        } else {
            countOfUnreadMessages.setCountOfUnreadMessages(
                    messageService.getCountOfUnreadMessagesRequest(
                            sessionService.asScoreUser(requester)));
        }
        return countOfUnreadMessages;
    }
}
