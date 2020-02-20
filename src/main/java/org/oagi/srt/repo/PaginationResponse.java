package org.oagi.srt.repo;

import java.util.List;

public class PaginationResponse<E> {

    private int pageCount;
    private List<E> result;

    public PaginationResponse(int pageCount, List<E> result) {
        this.pageCount = pageCount;
        this.result = result;
    }

    public int getPageCount() {
        return pageCount;
    }

    public List<E> getResult() {
        return result;
    }
}
