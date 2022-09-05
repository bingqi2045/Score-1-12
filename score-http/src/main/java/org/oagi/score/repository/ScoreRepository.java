package org.oagi.score.repository;

import java.util.Collections;
import java.util.List;

public interface ScoreRepository<T, PK> {

    List<T> findAll();

    default List<T> findAllByReleaseId(String releaseId) {
        return Collections.emptyList();
    }

    T findById(PK id);

}
