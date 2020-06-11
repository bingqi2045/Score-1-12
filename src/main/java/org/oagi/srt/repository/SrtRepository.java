package org.oagi.srt.repository;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public interface SrtRepository<T> {

    List<T> findAll();

    default List<T> findAllByReleaseId(BigInteger releaseId) {
        return Collections.emptyList();
    }

    T findById(BigInteger id);

}
