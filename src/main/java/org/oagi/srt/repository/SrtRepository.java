package org.oagi.srt.repository;

import java.math.BigInteger;
import java.util.List;

public interface SrtRepository<T> {

    List<T> findAll();

    T findById(BigInteger id);

}
