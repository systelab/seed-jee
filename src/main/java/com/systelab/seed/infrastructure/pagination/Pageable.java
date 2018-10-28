package com.systelab.seed.infrastructure.pagination;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Pageable {

    @Getter
    private int pageNumber;

    @Getter
    private int pageSize;

    public Pageable() {
        this.pageNumber = 1;
        this.pageSize = 100;
    }

    public Pageable(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
