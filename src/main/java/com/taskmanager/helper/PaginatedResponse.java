package com.taskmanager.helper;

import lombok.Data;
import java.util.List;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class PaginatedResponse<T> {

    @Schema(description = "Page content.")
    private List<T> content;
    @Schema(description = "Zero-based current page number.", example = "0")
    private int pageNumber;
    @Schema(description = "Number of items per page.", example = "20")
    private int pageSize;
    @Schema(description = "Total number of elements across all pages.", example = "42")
    private long totalElements;
    @Schema(description = "Total number of pages.", example = "3")
    private int totalPages;
    @Schema(description = "Whether this is the last page.", example = "false")
    private boolean last;

    public PaginatedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}
