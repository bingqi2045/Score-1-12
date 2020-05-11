package org.oagi.srt.repo.domain;

import org.jooq.DSLContext;
import static org.oagi.srt.entity.jooq.Tables.*;

import org.jooq.types.ULong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionSnapshotResolver {

    @Autowired
    DSLContext dslContext;

    public String getNamespaceUrl(ULong namespaceId) {
        return dslContext.select(NAMESPACE.URI)
                .from(NAMESPACE)
                .where(NAMESPACE.NAMESPACE_ID.eq(namespaceId)).fetchOneInto(String.class);
    }

    public String getUserLoginId(ULong userId) {
        return dslContext.select(APP_USER.LOGIN_ID)
                .from(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(userId)).fetchOneInto(String.class);
    }

    public String getAccObjectClass(ULong accId) {
        return dslContext.select(ACC.OBJECT_CLASS_TERM)
                .from(ACC)
                .where(ACC.ACC_ID.eq(accId)).fetchOneInto(String.class);
    }

    public String getDtDataTypeTerm(ULong bdtId) {
        return dslContext.select(DT.DATA_TYPE_TERM)
                .from(DT)
                .where(DT.DT_ID.eq(bdtId)).fetchOneInto(String.class);
    }
}
