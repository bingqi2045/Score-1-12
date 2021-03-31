package org.oagi.score.repo.api.impl.jooq.agency;

import org.jooq.DSLContext;
import org.oagi.score.repo.api.agency.AgencyIdListReadRepository;
import org.oagi.score.repo.api.agency.AgencyIdListWriteRepository;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;

public class JooqAgencyIdListWriteRepository
        extends JooqScoreRepository
        implements AgencyIdListWriteRepository {

    public JooqAgencyIdListWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }
}
