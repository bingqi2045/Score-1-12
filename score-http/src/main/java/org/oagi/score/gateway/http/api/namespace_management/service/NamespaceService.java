package org.oagi.score.gateway.http.api.namespace_management.service;

import org.jooq.DSLContext;
import org.jooq.Record5;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.repo.entity.jooq.tables.records.NamespaceRecord;
import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.namespace_management.data.Namespace;
import org.oagi.score.gateway.http.api.namespace_management.data.NamespaceList;
import org.oagi.score.gateway.http.api.namespace_management.data.NamespaceListRequest;
import org.oagi.score.gateway.http.api.namespace_management.data.SimpleNamespace;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.component.namespace.NamespaceReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.entity.jooq.Tables.NAMESPACE;

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

    public PageResponse<NamespaceList> getNamespaceList(AuthenticatedPrincipal user, NamespaceListRequest request) {
        AppUser requester = sessionService.getAppUser(user);
        return readRepository.fetch(requester, request);
    }

    public Namespace getNamespace(AuthenticatedPrincipal user, BigInteger namespaceId) {
        BigInteger userId = sessionService.userId(user);

        Record5<ULong, String, String, String, ULong> result =
                dslContext.select(NAMESPACE.NAMESPACE_ID,
                        NAMESPACE.URI,
                        NAMESPACE.PREFIX,
                        NAMESPACE.DESCRIPTION,
                        NAMESPACE.OWNER_USER_ID)
                        .from(NAMESPACE)
                        .where(NAMESPACE.NAMESPACE_ID.eq(ULong.valueOf(namespaceId)))
                        .fetchOne();

        Namespace namespace = new Namespace();
        namespace.setNamespaceId(result.get(NAMESPACE.NAMESPACE_ID).toBigInteger());
        namespace.setUri(result.get(NAMESPACE.URI));
        namespace.setPrefix(result.get(NAMESPACE.PREFIX));
        namespace.setDescription(result.get(NAMESPACE.DESCRIPTION));
        namespace.setCanEdit(result.get(NAMESPACE.OWNER_USER_ID).toBigInteger().equals(userId));
        return namespace;
    }

    @Transactional
    public BigInteger create(AuthenticatedPrincipal user, Namespace namespace) {
        String uri = namespace.getUri();
        boolean isExist = dslContext.selectCount()
                .from(NAMESPACE)
                .where(NAMESPACE.URI.eq(uri))
                .fetchOneInto(Integer.class) > 0;
        if (isExist) {
            throw new IllegalArgumentException("Namespace '" + uri + "' exists.");
        }

        AppUser requester = sessionService.getAppUser(user);
        BigInteger userId = requester.getAppUserId();
        LocalDateTime timestamp = LocalDateTime.now();

        NamespaceRecord namespaceRecord = new NamespaceRecord();
        namespaceRecord.setUri(namespace.getUri());
        namespaceRecord.setPrefix(namespace.getPrefix());
        namespaceRecord.setDescription(namespace.getDescription());
        namespaceRecord.setIsStdNmsp((byte) (requester.isDeveloper() ? 1 : 0));
        namespaceRecord.setOwnerUserId(ULong.valueOf(userId));
        namespaceRecord.setCreatedBy(ULong.valueOf(userId));
        namespaceRecord.setLastUpdatedBy(ULong.valueOf(userId));
        namespaceRecord.setCreationTimestamp(timestamp);
        namespaceRecord.setLastUpdateTimestamp(timestamp);

        return dslContext.insertInto(NAMESPACE)
                .set(namespaceRecord)
                .returning(NAMESPACE.NAMESPACE_ID)
                .fetchOne().getNamespaceId().toBigInteger();
    }

    @Transactional
    public void update(AuthenticatedPrincipal user, Namespace namespace) {
        String uri = namespace.getUri();
        boolean isExist = dslContext.selectCount()
                .from(NAMESPACE)
                .where(and(
                        NAMESPACE.URI.eq(uri),
                        NAMESPACE.NAMESPACE_ID.notEqual(ULong.valueOf(namespace.getNamespaceId()))
                ))
                .fetchOneInto(Integer.class) > 0;
        if (isExist) {
            throw new IllegalArgumentException("Namespace '" + uri + "' exists.");
        }

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

    @Transactional
    public void discard(AuthenticatedPrincipal user, BigInteger namespaceId) {
        ULong userId = ULong.valueOf(sessionService.userId(user));

        NamespaceRecord namespaceRecord = dslContext.selectFrom(NAMESPACE)
                .where(NAMESPACE.NAMESPACE_ID.eq(ULong.valueOf(namespaceId)))
                .fetchOptional().orElse(null);
        if (namespaceRecord == null) {
            throw new EmptyResultDataAccessException(1);
        }

        if (!namespaceRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("Access is denied");
        }

        namespaceRecord.delete();
    }
}
