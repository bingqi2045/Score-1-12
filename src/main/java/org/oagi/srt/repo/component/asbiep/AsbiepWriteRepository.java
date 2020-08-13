package org.oagi.srt.repo.component.asbiep;

import org.jooq.DSLContext;
import org.jooq.UpdateSetStep;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.AsbiepRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ABIE;
import static org.oagi.srt.entity.jooq.Tables.ASBIEP;
import static org.oagi.srt.gateway.http.helper.Utility.emptyToNull;

@Repository
public class AsbiepWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AsbiepReadRepository readRepository;

    public AsbiepNode.Asbiep upsertAsbiep(UpsertAsbiepRequest request) {
        AsbiepNode.Asbiep asbiep = request.getAsbiep();
        ULong topLevelAsbiepId = ULong.valueOf(request.getTopLevelAbieId());
        ULong refTopLevelAbieId = (request.getRefTopLevelAbieId() != null) ? ULong.valueOf(request.getRefTopLevelAbieId()) : null;
        String hashPath = asbiep.getHashPath();
        AsbiepRecord asbiepRecord = dslContext.selectFrom(ASBIEP)
                .where(and(
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                        ASBIEP.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (asbiepRecord == null) {
            asbiepRecord = new AsbiepRecord();
            asbiepRecord.setGuid(SrtGuid.randomGuid());
            asbiepRecord.setBasedAsccpManifestId(ULong.valueOf(asbiep.getBasedAsccpManifestId()));
            asbiepRecord.setPath(asbiep.getPath());
            asbiepRecord.setHashPath(hashPath);
            if (request.getRoleOfAbieId() != null) {
                asbiepRecord.setRoleOfAbieId(ULong.valueOf(request.getRoleOfAbieId()));
            } else {
                asbiepRecord.setRoleOfAbieId(dslContext.select(ABIE.ABIE_ID)
                        .from(ABIE)
                        .where(and(
                                ABIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq((refTopLevelAbieId != null) ? refTopLevelAbieId : topLevelAsbiepId),
                                ABIE.HASH_PATH.eq(asbiep.getRoleOfAbieHashPath())
                        ))
                        .fetchOneInto(ULong.class));
            }

            asbiepRecord.setDefinition(asbiep.getDefinition());
            asbiepRecord.setRemark(asbiep.getRemark());
            asbiepRecord.setBizTerm(asbiep.getBizTerm());

            asbiepRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

            asbiepRecord.setCreatedBy(requesterId);
            asbiepRecord.setLastUpdatedBy(requesterId);
            asbiepRecord.setCreationTimestamp(request.getLocalDateTime());
            asbiepRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            asbiepRecord.setAsbiepId(
                    dslContext.insertInto(ASBIEP)
                            .set(asbiepRecord)
                            .returning(ASBIEP.ASBIEP_ID)
                            .fetchOne().getAsbiepId()
            );
        } else {
            if (asbiep.getDefinition() != null) {
                asbiepRecord.setDefinition(emptyToNull(asbiep.getDefinition()));
            }

            if (asbiep.getRemark() != null) {
                asbiepRecord.setRemark(emptyToNull(asbiep.getRemark()));
            }

            if (asbiep.getBizTerm() != null) {
                asbiepRecord.setBizTerm(emptyToNull(asbiep.getBizTerm()));
            }

            if (asbiepRecord.changed()) {
                asbiepRecord.setLastUpdatedBy(requesterId);
                asbiepRecord.setLastUpdateTimestamp(request.getLocalDateTime());
                asbiepRecord.update(ASBIEP.DEFINITION,
                        ASBIEP.REMARK,
                        ASBIEP.BIZ_TERM,
                        ASBIEP.LAST_UPDATED_BY,
                        ASBIEP.LAST_UPDATE_TIMESTAMP);
            }

        }

        return readRepository.getAsbiep(request.getTopLevelAbieId(), hashPath);
    }
    
}
