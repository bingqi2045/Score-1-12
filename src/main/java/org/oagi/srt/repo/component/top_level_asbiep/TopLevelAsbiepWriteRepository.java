package org.oagi.srt.repo.component.top_level_asbiep;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.AbieRecord;
import org.oagi.srt.entity.jooq.tables.records.TopLevelAsbiepRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.component.abie.AbieNode;
import org.oagi.srt.repo.component.abie.AbieReadRepository;
import org.oagi.srt.repo.component.abie.UpsertAbieRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ABIE;
import static org.oagi.srt.entity.jooq.Tables.TOP_LEVEL_ASBIEP;
import static org.oagi.srt.gateway.http.helper.Utility.emptyToNull;

@Repository
public class TopLevelAsbiepWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    public void updateTopLevelAsbiep(UpdateTopLevelAsbiepRequest request) {

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        TopLevelAsbiepRecord record = dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(request.getTopLevelAsbiepId())))
                .fetchOptional().orElse(null);

        if (record == null) {
            throw new IllegalArgumentException("Unknown Top Level BIE.");
        }

        if (!requesterId.equals(record.getOwnerUserId())) {
            throw new IllegalArgumentException("Only the owner can modify it.");
        }

        if (request.getStatus() != null) {
            record.setStatus(emptyToNull(request.getStatus()));
        }

        if (request.getVersion() != null) {
            record.setVersion(emptyToNull(request.getVersion()));
        }

        if (record.changed()) {
            record.setLastUpdateTimestamp(request.getLocalDateTime());
            record.update(TOP_LEVEL_ASBIEP.STATUS,
                    TOP_LEVEL_ASBIEP.VERSION,
                    TOP_LEVEL_ASBIEP.LAST_UPDATE_TIMESTAMP);
        }
    }

}
