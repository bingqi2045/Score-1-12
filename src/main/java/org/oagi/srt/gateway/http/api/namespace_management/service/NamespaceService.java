package org.oagi.srt.gateway.http.api.namespace_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.namespace_management.data.Namespace;
import org.oagi.srt.gateway.http.api.namespace_management.data.NamespaceList;
import org.oagi.srt.gateway.http.api.namespace_management.data.NamespaceListRequest;
import org.oagi.srt.gateway.http.api.namespace_management.data.SimpleNamespace;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.component.namespace.NamespaceReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.NAMESPACE;

@Service
@Transactional(readOnly = true)
public class NamespaceService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private NamespaceReadRepository readRepository;

    public List<SimpleNamespace> getSimpleNamespaces() {
        return dslContext.select(NAMESPACE.NAMESPACE_ID, NAMESPACE.URI).from(NAMESPACE)
                .fetchInto(SimpleNamespace.class);
    }

    public PageResponse<NamespaceList> getNamespaceList(User user, NamespaceListRequest request) {
        AppUser requester = sessionService.getAppUser(user);
        return readRepository.fetch(requester, request);
    }

    public Namespace getNamespace(User user, BigInteger namespaceId) {
        BigInteger userId = sessionService.userId(user);

        Namespace namespace =
                dslContext.select(NAMESPACE.fields()).from(NAMESPACE)
                        .where(NAMESPACE.NAMESPACE_ID.eq(ULong.valueOf(namespaceId)))
                        .fetchOneInto(Namespace.class);
        if (!namespace.getOwnerUserId().equals(userId)) {
            throw new AccessDeniedException("Access is denied");
        }
        return namespace;
    }

    public BigInteger getNamespaceIdByUri(String uri) {
        return dslContext.select(NAMESPACE.NAMESPACE_ID).from(NAMESPACE)
                .where(NAMESPACE.URI.eq(uri))
                .fetchOptionalInto(BigInteger.class).orElse(BigInteger.ZERO);
    }

    @Transactional
    public void create(User user, Namespace namespace) {
        BigInteger userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        dslContext.insertInto(NAMESPACE,
                NAMESPACE.URI,
                NAMESPACE.CREATED_BY,
                NAMESPACE.LAST_UPDATED_BY,
                NAMESPACE.OWNER_USER_ID,
                NAMESPACE.CREATION_TIMESTAMP,
                NAMESPACE.DESCRIPTION,
                NAMESPACE.IS_STD_NMSP,
                NAMESPACE.LAST_UPDATE_TIMESTAMP,
                NAMESPACE.PREFIX)
                .values(namespace.getUri(), ULong.valueOf(userId), ULong.valueOf(userId),
                        ULong.valueOf(userId), timestamp, namespace.getDescription(), (byte) 0, timestamp,
                        namespace.getPrefix()).execute();
    }

    @Transactional
    public void update(User user, Namespace namespace) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();

        int res = dslContext.update(NAMESPACE)
                .set(NAMESPACE.URI, namespace.getUri())
                .set(NAMESPACE.PREFIX, namespace.getPrefix())
                .set(NAMESPACE.DESCRIPTION, namespace.getDescription())
                .set(NAMESPACE.LAST_UPDATED_BY, userId)
                .set(NAMESPACE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(NAMESPACE.OWNER_USER_ID.eq(userId),
                        NAMESPACE.NAMESPACE_ID.eq(ULong.valueOf(namespace.getNamespaceId()))).execute();

        if (res != 1) {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
