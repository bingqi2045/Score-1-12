package org.oagi.score.export.model;

import org.oagi.score.provider.ImportedDataProvider;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AccRecord;

public class ACCComplexType extends ACC {

    ACCComplexType(AccRecord acc, ACC basedAcc,
                   ImportedDataProvider importedDataProvider) {
        super(acc, basedAcc, importedDataProvider);
    }
}
