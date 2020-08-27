package org.oagi.score.repo.component.top_level_asbiep;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.repo.entity.jooq.tables.records.TopLevelAsbiepRecord;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.oagi.score.repo.entity.jooq.Tables.TOP_LEVEL_ASBIEP;
import static org.oagi.score.gateway.http.helper.Utility.emptyToNull;

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
