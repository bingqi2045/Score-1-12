package org.oagi.score.provider;

import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccRecord;

import java.util.List;

public interface CoreComponentProvider {

    public List<BccRecord> getBCCs(long accId);

    public List<BccRecord> getBCCsWithoutAttributes(long accId);

    public List<AsccRecord> getASCCs(long accId);

}
