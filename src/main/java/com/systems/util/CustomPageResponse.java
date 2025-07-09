package com.systems.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPageResponse<T> {
    private java.util.List<T> content;
    private CustomPageable pageable;
    private boolean last;
    private int totalElements;
    private int totalPages;
    private boolean first;
    private int size;
    private int number;
    private Sort sort;
    private int numberOfElements;
    private boolean empty;

    public CustomPageResponse(org.springframework.data.domain.Page<T> page, int displayPageNumber) {
        this.content = page.getContent();
        this.totalElements = (int) page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.size = page.getSize();
        this.number = displayPageNumber; // Número de página que se muestra (base-1)
        this.sort = page.getSort();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();

        // Ajustar first/last basado en el número de página mostrado (base-1)
        this.first = (displayPageNumber == 1);
        this.last = (displayPageNumber == this.totalPages);

        // Crear pageable personalizado con offset correcto
        long correctOffset = (long) (displayPageNumber - 1) * page.getSize();
        this.pageable = new CustomPageable(displayPageNumber, page.getSize(), page.getSort(), correctOffset);
    }
}
