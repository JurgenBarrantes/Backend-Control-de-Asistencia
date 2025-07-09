package com.systems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPageable {
    private int pageNumber;
    private int pageSize;
    private Sort sort;
    private long offset;
    private boolean paged = true;
    private boolean unpaged = false;

    public CustomPageable(int pageNumber, int pageSize, Sort sort, long offset) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
        this.offset = offset;
    }
}
