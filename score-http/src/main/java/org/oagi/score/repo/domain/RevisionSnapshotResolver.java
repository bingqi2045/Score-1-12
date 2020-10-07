package org.oagi.score.repo.domain;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.types.ULong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class RevisionSnapshotResolver {

    @Autowired
    private DSLContext dslContext;

    public String getNamespaceUrl(ULong namespaceId) {
        if (namespaceId == null || namespaceId.longValue() <= 0L) {
            return "";
        }
        return dslContext.select(NAMESPACE.URI)
                .from(NAMESPACE)
                .where(NAMESPACE.NAMESPACE_ID.eq(namespaceId))
                .fetchOneInto(String.class);
    }

    public String getUserLoginId(ULong userId) {
        if (userId == null || userId.longValue() <= 0L) {
            return "";
        }
        return dslContext.select(APP_USER.LOGIN_ID)
                .from(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(userId))
                .fetchOneInto(String.class);
    }

    public String getAccObjectClassTerm(ULong accId) {
        if (accId == null || accId.longValue() <= 0L) {
            return "";
        }
        return dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC)
                .where(ACC.ACC_ID.eq(accId))
                .fetchOneInto(String.class);
    }

    public String getAsccpDen(ULong asccpId) {
        if (asccpId == null || asccpId.longValue() <= 0L) {
            return "";
        }
        Record2<String, String> result = dslContext.select(ASCCP.PROPERTY_TERM, ACC.OBJECT_CLASS_TERM)
                .from(ASCCP)
                .join(ACC).on(ASCCP.ROLE_OF_ACC_ID.eq(ACC.ACC_ID))
                .where(ASCCP.ASCCP_ID.eq(asccpId))
                .fetchOne();
        return result.value1() + ". " + result.value2();
    }

    public String getAsccpPropertyTerm(ULong asccpId) {
        if (asccpId == null || asccpId.longValue() <= 0L) {
            return "";
        }
        return dslContext.select(ASCCP.PROPERTY_TERM)
                .from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(asccpId))
                .fetchOneInto(String.class);
    }

    public String getBccpDen(ULong bccpId) {
        if (bccpId == null || bccpId.longValue() <= 0L) {
            return "";
        }
        Record2<String, String> result = dslContext.select(BCCP.PROPERTY_TERM, BCCP.REPRESENTATION_TERM)
                .from(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpId))
                .fetchOne();
        return result.value1() + ". " + result.value2();
    }

    public String getBccpPropertyTerm(ULong bccpId) {
        if (bccpId == null || bccpId.longValue() <= 0L) {
            return "";
        }
        return dslContext.select(BCCP.PROPERTY_TERM)
                .from(BCCP)
                .where(BCCP.BCCP_ID.eq(bccpId))
                .fetchOneInto(String.class);
    }

    public String getDtDen(ULong bdtId) {
        if (bdtId == null || bdtId.longValue() <= 0L) {
            return "";
        }
        return dslContext.select(DT.DEN)
                .from(DT)
                .where(DT.DT_ID.eq(bdtId)).fetchOneInto(String.class);
    }
}
