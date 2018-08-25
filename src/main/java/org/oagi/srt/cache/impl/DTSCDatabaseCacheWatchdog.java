package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.DTSC;
import org.oagi.srt.repository.DTSCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DTSCDatabaseCacheWatchdog extends DatabaseCacheWatchdog<DTSC> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public DTSCDatabaseCacheWatchdog(@Autowired DTSCRepository delegate) {
        super("dt_sc", DTSC.class, delegate);
    }

}
