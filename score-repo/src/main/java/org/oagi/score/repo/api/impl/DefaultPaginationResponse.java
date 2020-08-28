package org.oagi.score.repo.api.impl;

import org.oagi.score.repo.api.base.PaginationResponse;

import java.util.List;

public class DefaultPaginationResponse<T> implements PaginationResponse<T> {

    private List<T> list;
    private int page;
    private int size;
    private int length;

    @Override
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
