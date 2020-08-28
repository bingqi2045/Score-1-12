package org.oagi.score.repo.api.base;

import java.util.List;

public interface PaginationResponse<T> {

    List<T> getList();

    int getPage();

    int getSize();

    int getLength();

}
