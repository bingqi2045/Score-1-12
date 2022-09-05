package org.oagi.score.gateway.http.api.namespace_management.service;

import org.jooq.DSLContext;
import org.jooq.Record6;
import org.oagi.score.gateway.http.api.namespace_management.data.Namespace;
import org.oagi.score.gateway.http.api.namespace_management.data.NamespaceList;
import org.oagi.score.gateway.http.api.namespace_management.data.NamespaceListRequest;
import org.oagi.score.gateway.http.api.namespace_management.data.SimpleNamespace;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AppUserRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.NamespaceRecord;
import org.oagi.score.repo.component.namespace.NamespaceReadRepository;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.service.common.data.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Service
@Transactional(readOnly = true)
public class NamespaceService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private NamespaceReadRepository readRepository;

    public List<SimpleNamespace> getSimpleNamespaces(AuthenticatedPrincipal user) {
        AppUser requester = sessionService.getAppUserByUsername(user);
        return dslContext.select(NAMESPACE.NAMESPACE_ID, NAMESPACE.URI, NAMESPACE.IS_STD_NMSP.as("standard"))
                .from(NAMESPACE)
                .fetchInto(SimpleNamespace.class);
    }

    public PageResponse<NamespaceList> getNamespaceList(AuthenticatedPrincipal user, NamespaceListRequest request) {
        AppUser requester = sessionService.getAppUserByUsername(user);
        return readRepository.fetch(requester, request);
    }

    public Namespace getNamespace(AuthenticatedPrincipal user, String namespaceId) {
        String userId = sessionService.userId(user);

        Record6<String, String, String, String, Byte, String> result =
                dslContext.select(NAMESPACE.NAMESPACE_ID,
                        NAMESPACE.URI,
                        NAMESPACE.PREFIX,
                        NAMESPACE.DESCRIPTION,
                        NAMESPACE.IS_STD_NMSP,
                        NAMESPACE.OWNER_USER_ID)
                        .from(NAMESPACE)
                        .where(NAMESPACE.NAMESPACE_ID.eq(namespaceId))
                        .fetchOne();

        Namespace namespace = new Namespace();
        namespace.setNamespaceId(result.get(NAMESPACE.NAMESPACE_ID));
        namespace.setUri(result.get(NAMESPACE.URI));
        namespace.setPrefix(result.get(NAMESPACE.PREFIX));
        namespace.setDescription(result.get(NAMESPACE.DESCRIPTION));
        namespace.setStd(result.get(NAMESPACE.IS_STD_NMSP) == 1);
        namespace.setCanEdit(result.get(NAMESPACE.OWNER_USER_ID).equals(userId));
        return namespace;
    }

    @Transactional
    public String create(AuthenticatedPrincipal user, Namespace namespace) {
        String uri = namespace.getUri();
        boolean isURIExist = dslContext.selectCount()
                .from(NAMESPACE)
                .where(NAMESPACE.URI.eq(uri))
                .fetchOneInto(Integer.class) > 0;
        if (isURIExist) {
            throw new IllegalArgumentException("Namespace '" + uri + "' exists.");
        }
        boolean isPrefixExist = dslContext.selectCount()
                .from(NAMESPACE)
                .where(NAMESPACE.PREFIX.eq(namespace.getPrefix()))
                .fetchOneInto(Integer.class) > 0;
        if (isPrefixExist) {
            throw new IllegalArgumentException("Namespace Prefix '" + namespace.getPrefix() + "' exists.");
        }

        AppUser requester = sessionService.getAppUserByUsername(user);
        String userId = requester.getAppUserId();
        LocalDateTime timestamp = LocalDateTime.now();

        NamespaceRecord namespaceRecord = new NamespaceRecord();
        namespaceRecord.setNamespaceId(UUID.randomUUID().toString());
        namespaceRecord.setUri(namespace.getUri());
        namespaceRecord.setPrefix(namespace.getPrefix());
        namespaceRecord.setDescription(namespace.getDescription());
        namespaceRecord.setIsStdNmsp((byte) (requester.isDeveloper() ? 1 : 0));
        namespaceRecord.setOwnerUserId(userId);
        namespaceRecord.setCreatedBy(userId);
        namespaceRecord.setLastUpdatedBy(userId);
        namespaceRecord.setCreationTimestamp(timestamp);
        namespaceRecord.setLastUpdateTimestamp(timestamp);

        dslContext.insertInto(NAMESPACE)
                .set(namespaceRecord)
                .execute();

        return namespaceRecord.getNamespaceId();
    }

    @Transactional
    public void update(AuthenticatedPrincipal user, Namespace namespace) {
        String uri = namespace.getUri();
        boolean isUriExist = dslContext.selectCount()
                .from(NAMESPACE)
                .where(and(
                        NAMESPACE.URI.eq(uri),
                        NAMESPACE.NAMESPACE_ID.notEqual(namespace.getNamespaceId())
                ))
                .fetchOneInto(Integer.class) > 0;
        if (isUriExist) {
            throw new IllegalArgumentException("Namespace URI '" + uri + "' exists.");
        }

        boolean isPrefixExist = dslContext.selectCount()
                .from(NAMESPACE)
                .where(and(
                        NAMESPACE.PREFIX.eq(namespace.getPrefix()),
                        NAMESPACE.NAMESPACE_ID.notEqual(namespace.getNamespaceId())
                ))
                .fetchOneInto(Integer.class) > 0;
        if (isPrefixExist) {
            throw new IllegalArgumentException("Namespace Prefix '" + namespace.getPrefix() + "' exists.");
        }

        String userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        int res = dslContext.update(NAMESPACE)
                .set(NAMESPACE.URI, namespace.getUri())
                .set(NAMESPACE.PREFIX, namespace.getPrefix())
                .set(NAMESPACE.DESCRIPTION, namespace.getDescription())
                .set(NAMESPACE.LAST_UPDATED_BY, userId)
                .set(NAMESPACE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(NAMESPACE.OWNER_USER_ID.eq(userId),
                        NAMESPACE.NAMESPACE_ID.eq(namespace.getNamespaceId())).execute();

        if (res != 1) {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Transactional
    public void transferOwnership(AuthenticatedPrincipal user, String namespaceId, String targetLoginId) {
        AppUser owner = sessionService.getAppUserByUsername(user.getName());
        LocalDateTime timestamp = LocalDateTime.now();

        AppUserRecord targetUserRecord = dslContext.selectFrom(APP_USER)
                .where(APP_USER.LOGIN_ID.eq(targetLoginId)).fetchOne();

        Namespace namespace = getNamespace(user, namespaceId);

        boolean isDeveloper = targetUserRecord.getIsDeveloper() == (byte) 1;

        if (namespace.isStd() != isDeveloper) {
            if (namespace.isStd()) {
                throw new IllegalArgumentException("Standard namespace can not transfer to End User.");
            }
            throw new IllegalArgumentException("Non standard namespace can not transfer to Developer.");
        }

        int res = dslContext.update(NAMESPACE)
                .set(NAMESPACE.OWNER_USER_ID, targetUserRecord.getAppUserId())
                .set(NAMESPACE.LAST_UPDATED_BY, owner.getAppUserId())
                .set(NAMESPACE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(NAMESPACE.OWNER_USER_ID.eq(owner.getAppUserId()),
                        NAMESPACE.NAMESPACE_ID.eq(namespace.getNamespaceId())).execute();

        if (res != 1) {
            throw new AccessDeniedException("Access is denied");
        }
    }

    @Transactional
    public void discard(AuthenticatedPrincipal user, String namespaceId) {
        String userId = sessionService.userId(user);

        NamespaceRecord namespaceRecord = dslContext.selectFrom(NAMESPACE)
                .where(NAMESPACE.NAMESPACE_ID.eq(namespaceId))
                .fetchOptional().orElse(null);
        if (namespaceRecord == null) {
            throw new EmptyResultDataAccessException(1);
        }

        if (!namespaceRecord.getOwnerUserId().equals(userId)) {
            throw new IllegalArgumentException("Access is denied");
        }

        Integer referenced = 0;
        referenced += dslContext.selectCount().from(ACC).where(ACC.NAMESPACE_ID.eq(namespaceRecord.getNamespaceId()))
                .fetchOptionalInto(Integer.class).orElse(0);
        referenced += dslContext.selectCount().from(ASCCP).where(ASCCP.NAMESPACE_ID.eq(namespaceRecord.getNamespaceId()))
                .fetchOptionalInto(Integer.class).orElse(0);
        referenced += dslContext.selectCount().from(BCCP).where(BCCP.NAMESPACE_ID.eq(namespaceRecord.getNamespaceId()))
                .fetchOptionalInto(Integer.class).orElse(0);
        referenced += dslContext.selectCount().from(CODE_LIST).where(CODE_LIST.NAMESPACE_ID.eq(namespaceRecord.getNamespaceId()))
                .fetchOptionalInto(Integer.class).orElse(0);
        referenced += dslContext.selectCount().from(RELEASE).where(RELEASE.NAMESPACE_ID.eq(namespaceRecord.getNamespaceId()))
                .fetchOptionalInto(Integer.class).orElse(0);
        referenced += dslContext.selectCount().from(AGENCY_ID_LIST).where(AGENCY_ID_LIST.NAMESPACE_ID.eq(namespaceRecord.getNamespaceId()))
                .fetchOptionalInto(Integer.class).orElse(0);
        referenced += dslContext.selectCount().from(DT).where(DT.NAMESPACE_ID.eq(namespaceRecord.getNamespaceId()))
                .fetchOptionalInto(Integer.class).orElse(0);

        if (referenced > 0) {
            throw new IllegalArgumentException("The namespace in use can not be discard.");
        }

        namespaceRecord.delete();
    }
}
