package org.oagi.srt.cache.impl;

import org.oagi.srt.cache.DatabaseCacheWatchdog;
import org.oagi.srt.data.ACC;
import org.oagi.srt.repository.ACCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ACCDatabaseCacheWatchdog extends DatabaseCacheWatchdog<ACC> {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ACCDatabaseCacheWatchdog(@Autowired ACCRepository delegate) {
        super("acc", ACC.class, delegate);
    }

}
