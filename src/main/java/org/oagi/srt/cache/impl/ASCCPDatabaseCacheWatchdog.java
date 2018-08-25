package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.ASCCP;
import org.oagi.srt.repository.ASCCPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ASCCPDatabaseCacheWatchdog extends DatabaseCacheWatchdog<ASCCP> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ASCCPDatabaseCacheWatchdog(@Autowired ASCCPRepository delegate) {
        super("asccp", ASCCP.class, delegate);
    }

}
