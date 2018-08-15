package org.oagi.srt.gateway.http.api.common.data;

import lombok.Data;

import java.util.List;

@Data
public class Pagination<T> {

    private List<T> list;
    private int page;
    private int size;
    private int length;

}
