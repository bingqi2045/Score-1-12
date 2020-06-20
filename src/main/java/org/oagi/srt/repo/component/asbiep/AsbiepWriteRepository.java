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
        ULong topLevelAbieId = ULong.valueOf(request.getTopLevelAbieId());
        ULong refTopLevelAbieId = (request.getRefTopLevelAbieId() != null) ? ULong.valueOf(request.getRefTopLevelAbieId()) : null;
        String hashPath = asbiep.getHashPath();
        AsbiepRecord asbiepRecord = dslContext.selectFrom(ASBIEP)
                .where(and(
                        ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                        ASBIEP.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (asbiepRecord == null) {
            asbiepRecord = new AsbiepRecord();
            asbiepRecord.setGuid(SrtGuid.randomGuid());
            asbiepRecord.setBasedAsccpManifestId(ULong.valueOf(asbiep.getBasedAsccpManifestId()));
            asbiepRecord.setHashPath(hashPath);
            if (request.getRoleOfAbieId() != null) {
                asbiepRecord.setRoleOfAbieId(ULong.valueOf(request.getRoleOfAbieId()));
            } else {
                asbiepRecord.setRoleOfAbieId(dslContext.select(ABIE.ABIE_ID)
                        .from(ABIE)
                        .where(and(
                                ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq((refTopLevelAbieId != null) ? refTopLevelAbieId : topLevelAbieId),
                                ABIE.HASH_PATH.eq(asbiep.getRoleOfAbieHashPath())
                        ))
                        .fetchOneInto(ULong.class));
            }

            asbiepRecord.setDefinition(asbiep.getDefinition());
            asbiepRecord.setRemark(asbiep.getRemark());
            asbiepRecord.setBizTerm(asbiep.getBizTerm());

            asbiepRecord.setOwnerTopLevelAbieId(topLevelAbieId);
            asbiepRecord.setRefTopLevelAbieId(refTopLevelAbieId);

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
            UpdateSetStep updateSetStep = dslContext.update(ASBIEP);
            if (!StringUtils.equals(asbiepRecord.getDefinition(), asbiep.getDefinition())) {
                if (!StringUtils.isEmpty(asbiep.getDefinition())) {
                    updateSetStep = updateSetStep.set(ASBIEP.DEFINITION, asbiep.getDefinition());
                } else {
                    updateSetStep = updateSetStep.setNull(ASBIEP.DEFINITION);
                }
            }
            if (!StringUtils.equals(asbiepRecord.getRemark(), asbiep.getRemark())) {
                if (!StringUtils.isEmpty(asbiep.getRemark())) {
                    updateSetStep = updateSetStep.set(ASBIEP.REMARK, asbiep.getRemark());
                } else {
                    updateSetStep = updateSetStep.setNull(ASBIEP.REMARK);
                }
            }
            if (!StringUtils.equals(asbiepRecord.getBizTerm(), asbiep.getBizTerm())) {
                if (!StringUtils.isEmpty(asbiep.getBizTerm())) {
                    updateSetStep = updateSetStep.set(ASBIEP.BIZ_TERM, asbiep.getBizTerm());
                } else {
                    updateSetStep = updateSetStep.setNull(ASBIEP.BIZ_TERM);
                }
            }
            if (request.getRoleOfAbieId() != null) {
                updateSetStep = updateSetStep.set(ASBIEP.ROLE_OF_ABIE_ID, ULong.valueOf(request.getRoleOfAbieId()));
            }
            if (request.getRefTopLevelAbieId() != null) {
                updateSetStep = updateSetStep.set(ASBIEP.REF_TOP_LEVEL_ABIE_ID, ULong.valueOf(request.getRefTopLevelAbieId()));
            } else {
                updateSetStep = updateSetStep.setNull(ASBIEP.REF_TOP_LEVEL_ABIE_ID);
            }

            updateSetStep.set(ASBIEP.LAST_UPDATED_BY, requesterId)
                    .set(ASBIEP.LAST_UPDATE_TIMESTAMP, request.getLocalDateTime())
                    .where(ASBIEP.ASBIEP_ID.eq(asbiepRecord.getAsbiepId()))
                    .execute();
        }

        return readRepository.getAsbiep(request.getTopLevelAbieId(), hashPath);
    }
    
}
