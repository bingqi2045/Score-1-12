package org.oagi.score.e2e.impl.api;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.oagi.score.e2e.api.APIFactory;
import org.oagi.score.e2e.api.ModuleSetAPI;
import org.oagi.score.e2e.obj.AppUserObject;
import org.oagi.score.e2e.obj.ModuleSetObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.oagi.score.e2e.impl.api.jooq.entity.Tables.*;

public class DSLContextModuleSetAPIImpl implements ModuleSetAPI {

    private final DSLContext dslContext;
    private final APIFactory apiFactory;

    public DSLContextModuleSetAPIImpl(DSLContext dslContext, APIFactory apiFactory) {
        this.dslContext = dslContext;
        this.apiFactory = apiFactory;
    }

    @Override
    public ModuleSetObject getTheLatestModuleSetCreatedBy(AppUserObject user) {
        ULong latestModuleSetIDByUser = dslContext.select(DSL.max(MODULE_SET.MODULE_SET_ID))
                .from(MODULE_SET)
                .where(MODULE_SET.CREATED_BY.eq(ULong.valueOf(user.getAppUserId())))
                .fetchOneInto(ULong.class);
        List<Field<?>> fields = new ArrayList();
        fields.addAll(Arrays.asList(MODULE_SET.fields()));
        return dslContext.select(fields)
                .from(MODULE_SET)
                .where(MODULE_SET.MODULE_SET_ID.eq(latestModuleSetIDByUser))
                .fetchOne(record -> moduleSetMapper(record));
    }

    @Override
    public List<ModuleSetObject> getAllModuleSets() {
        List<ModuleSetObject> moduleSets = new ArrayList<>();
        List<Field<?>> fields = new ArrayList();
        fields.addAll(Arrays.asList(MODULE_SET.fields()));
        Result<Record> records = dslContext.select(fields)
                .from(MODULE_SET)
                .fetch();
        for (Record r : records){
            ModuleSetObject moduleSet = moduleSetMapper(r);
            moduleSets.add(moduleSet);
        }
        return moduleSets;
    }

    private ModuleSetObject moduleSetMapper(Record record) {
        ModuleSetObject moduleSetObject = new ModuleSetObject();
        moduleSetObject.setModuleSetId(record.get(MODULE_SET.MODULE_SET_ID).toBigInteger());
        moduleSetObject.setName(record.get(MODULE_SET.NAME));
        moduleSetObject.setDescription(record.get(MODULE_SET.DESCRIPTION));
        return moduleSetObject;
    }
}
