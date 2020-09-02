package org.oagi.score.repo.api.base;

import org.oagi.score.repo.api.user.model.ScoreUser;

public class PaginationRequest<T> extends Request {

    public static int DEFAULT_PAGE_INDEX = 0;
    public static int DEFAULT_PAGE_SIZE = 10;

    private final Class<T> type;
    private String sortActive;
    private SortDirection sortDirection;
    private int pageIndex = DEFAULT_PAGE_INDEX;
    private int pageSize = DEFAULT_PAGE_SIZE;

    public PaginationRequest(ScoreUser requester, Class<T> type) {
        super(requester);
        this.type = type;
    }

    public Class<T> getType() {
        return this.type;
    }

    public String getSortActive() {
        return sortActive;
    }

    public void setSortActive(String sortActive) {
        this.sortActive = sortActive;
    }

    public SortDirection getSortDirection() {
        return (sortDirection == null) ? SortDirection.ASC : sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public final int getPageOffset() {
        int offset = this.getPageIndex() * this.getPageSize();
        return (offset < 0) ? -1 : offset;
    }

    public final boolean isPagination() {
        return this.getPageOffset() > -1;
    }
}
