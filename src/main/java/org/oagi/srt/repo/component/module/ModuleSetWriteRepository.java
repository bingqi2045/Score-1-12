package org.oagi.srt.repo.component.module;

import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.jooq.UpdateSetStep;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.ModuleSetRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.oagi.srt.entity.jooq.Tables.MODULE_SET;

@Repository
public class ModuleSetWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    public void updateModuleSet(UpdateModuleSetRepositoryRequest request) {
        ModuleSetRecord moduleSetRecord = dslContext.selectFrom(MODULE_SET)
                .where(MODULE_SET.MODULE_SET_ID.eq(ULong.valueOf(request.getModuleSetId())))
                .fetchOptional().orElse(null);
        if (moduleSetRecord == null) {
            throw new IllegalArgumentException("Can't found a module set with the module set id: " + request.getModuleSetId());
        }

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong userId = ULong.valueOf(user.getAppUserId());

        if (!user.isDeveloper()) {
            throw new IllegalArgumentException("Users who are not a developer cannot update the module set.");
        }

        LocalDateTime timestamp = request.getLocalDateTime();
        if (StringUtils.isEmpty(request.getName())) {
            throw new IllegalArgumentException("The 'name' property of the module set is not allowed an empty value.");
        }

        UpdateSetStep step = dslContext.update(MODULE_SET);
        if (!StringUtils.equals(moduleSetRecord.getName(), request.getName())) {
            step = step.set(MODULE_SET.NAME, request.getName());
        }
        if (!StringUtils.equals(moduleSetRecord.getDescription(), request.getDescription())) {
            if (StringUtils.isEmpty(request.getDescription())) {
                step = step.setNull(MODULE_SET.DESCRIPTION);
            } else {
                step = step.set(MODULE_SET.DESCRIPTION, request.getDescription());
            }
        }

        if (step instanceof UpdateSetMoreStep) {
            step.set(MODULE_SET.LAST_UPDATED_BY, userId)
                    .set(MODULE_SET.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(MODULE_SET.MODULE_SET_ID.eq(moduleSetRecord.getModuleSetId()))
                    .execute();
        }
    }
}
