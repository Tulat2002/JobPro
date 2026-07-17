package com.devanktu.jobpro.domain.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ResultPaginationDto {

    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        private int pages;
        private long total;
    }

}
